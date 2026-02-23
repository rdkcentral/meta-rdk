/**
 * @file libchronyctl.c
 * @brief Implementation of thread-safe chronyd control library using direct protocol
 */

#include "libchronyctl.h"
#include "candm.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <pthread.h>
#include <errno.h>
#include <unistd.h>
#include <sys/socket.h>
#include <sys/un.h>
#include <arpa/inet.h>
#include <netinet/in.h>
#include <math.h>
#include <netdb.h>
#include <sys/stat.h>
#include <stddef.h>

/* --- Internal State --- */

static pthread_mutex_t chronyctl_mutex = PTHREAD_MUTEX_INITIALIZER;
static int chronyctl_initialized = 0;
static uint32_t chrony_sequence = 0;

static const char *socket_paths[] = {
    "/var/run/chrony/chronyd.sock",
    "/run/chrony/chronyd.sock",
    NULL
};

/* --- Helper Functions --- */

#define FLOAT_EXP_BITS 7
#define FLOAT_COEF_BITS ((int)sizeof (int32_t) * 8 - FLOAT_EXP_BITS)

static double float_to_double(Float f) {
    int32_t exp, coef;
    uint32_t x = ntohl(f.f);

    exp = x >> FLOAT_COEF_BITS;
    if (exp >= 1 << (FLOAT_EXP_BITS - 1))
        exp -= 1 << FLOAT_EXP_BITS;
    exp -= FLOAT_COEF_BITS;

    coef = x % (1U << FLOAT_COEF_BITS);
    if (coef >= 1 << (FLOAT_COEF_BITS - 1))
        coef -= 1 << FLOAT_COEF_BITS;

    return coef * pow(2.0, exp);
}

static int parse_address(const char *address, IPAddr *ip) {
    struct addrinfo hints, *res;
    memset(&hints, 0, sizeof(hints));
    hints.ai_family = AF_INET; 
    
    memset(ip, 0, sizeof(*ip));
    if (getaddrinfo(address, NULL, &hints, &res) == 0) {
        struct sockaddr_in *sin = (struct sockaddr_in *)res->ai_addr;
        ip->addr.in4 = ntohl(sin->sin_addr.s_addr);
        ip->family = IPADDR_INET4;
        freeaddrinfo(res);
        return 0;
    }
    return -1;
}

static void ip_host_to_network(const IPAddr *src, IPAddr *dest) {
    memset(dest, 0, sizeof(IPAddr));
    dest->family = htons(src->family);
    if (src->family == IPADDR_INET4) {
        dest->addr.in4 = htonl(src->addr.in4);
    }
}

static int connect_to_chronyd(void) {
    int sockfd;
    struct timeval tv = { .tv_sec = 2, .tv_usec = 0 };

    sockfd = socket(AF_UNIX, SOCK_DGRAM, 0);
    if (sockfd < 0) return -1;

    struct sockaddr_un local_addr;
    memset(&local_addr, 0, sizeof(local_addr));
    local_addr.sun_family = AF_UNIX;
    snprintf(local_addr.sun_path, sizeof(local_addr.sun_path), "/var/run/chrony/chronyc.%d.sock", getpid());
    
    unlink(local_addr.sun_path);
    if (bind(sockfd, (struct sockaddr *)&local_addr, sizeof(local_addr)) < 0) {
        snprintf(local_addr.sun_path, sizeof(local_addr.sun_path), "/tmp/chronyc.%d.sock", getpid());
        unlink(local_addr.sun_path);
        if (bind(sockfd, (struct sockaddr *)&local_addr, sizeof(local_addr)) < 0) {
            close(sockfd);
            return -1;
        }
    }
    chmod(local_addr.sun_path, 0600);

    for (int i = 0; socket_paths[i] != NULL; i++) {
        struct sockaddr_un addr;
        memset(&addr, 0, sizeof(addr));
        addr.sun_family = AF_UNIX;
        strncpy(addr.sun_path, socket_paths[i], sizeof(addr.sun_path) - 1);
        if (connect(sockfd, (struct sockaddr *)&addr, sizeof(addr)) == 0) {
            setsockopt(sockfd, SOL_SOCKET, SO_RCVTIMEO, &tv, sizeof(tv));
            return sockfd;
        }
    }

    /* Fallback to UDP port 323 */
    sockfd = socket(AF_INET, SOCK_DGRAM, 0);
    if (sockfd >= 0) {
        struct sockaddr_in udp_addr;
        memset(&udp_addr, 0, sizeof(udp_addr));
        udp_addr.sin_family      = AF_INET;
        udp_addr.sin_port        = htons(323);
        udp_addr.sin_addr.s_addr = inet_addr("127.0.0.1");
        if (connect(sockfd, (struct sockaddr *)&udp_addr, sizeof(udp_addr)) == 0) {
            setsockopt(sockfd, SOL_SOCKET, SO_RCVTIMEO, &tv, sizeof(tv));
            return sockfd;
        }
        close(sockfd);
    }

    return -1;
}

/* 
 * Simplified packet length calculation for Chrony version 6.
 * Values taken from observation and pktlength.c logic.
 */
static size_t get_request_length(uint16_t command) {
    switch (command) {
        case REQ_TRACKING:   return 104; // Header(20) + Data(4) + Padding(80) 
        case REQ_MAKESTEP:   return 28;  // Header(20) + Data(4) + Padding(4)
        case REQ_ADD_SOURCE: return 520; // Observation for add server 
        case REQ_DEL_SOURCE: return 40;  // Observation from strace (Header 20 + IPAddr 20)
        case REQ_MODIFY_MINPOLL: return 44; // Observation from strace
        case REQ_MODIFY_MAXPOLL: return 44; // Observation from strace
        default: return sizeof(CMD_Request);
    }
}

static int send_request(int sockfd, uint16_t command, void *data, size_t data_size) {
    CMD_Request req;
    memset(&req, 0, sizeof(req));
    
    req.version  = PROTO_VERSION_NUMBER;
    req.pkt_type = PKT_TYPE_CMD_REQUEST;
    req.command  = htons(command);
    req.sequence = htonl(chrony_sequence++);
    
    if (data && data_size > 0) {
        memcpy(&req.data, data, data_size);
    }
    
    size_t len = get_request_length(command);
    ssize_t sent = send(sockfd, &req, len, 0);
    return (sent == (ssize_t)len) ? 0 : -1;
}

static int receive_reply(int sockfd, uint16_t expected_reply, void *data, size_t data_size) {
    CMD_Reply reply;
    memset(&reply, 0, sizeof(reply));
    ssize_t received = recv(sockfd, &reply, sizeof(reply), 0);
    
    if (received < 0) {
        return CHRONYCTL_ERROR_EXEC;
    }
    
    if (received < (ssize_t)offsetof(CMD_Reply, data)) {
        return CHRONYCTL_ERROR_EXEC;
    }
    
    if (reply.version != PROTO_VERSION_NUMBER || reply.pkt_type != PKT_TYPE_CMD_REPLY) {
        return CHRONYCTL_ERROR_EXEC;
    }
    
    uint16_t st = ntohs(reply.status);
    if (st != STT_SUCCESS) {
        if (st == STT_UNAUTH) return CHRONYCTL_ERROR_UNAUTH;
        if (st == STT_NOSUCHSOURCE) return CHRONYCTL_ERROR_EXEC; // Map to -3 for now but can be improved
        return CHRONYCTL_ERROR_EXEC;
    }

    uint16_t rpy = ntohs(reply.reply);
    if (rpy != expected_reply && expected_reply != RPY_NULL) {
        // Some flexibility allowed here
    }
    
    if (data && data_size > 0) {
        memcpy(data, &reply.data, data_size);
    }
    
    return CHRONYCTL_SUCCESS;
}

static void cleanup_local_socket() {
    char local_path[128];
    snprintf(local_path, sizeof(local_path), "/var/run/chrony/chronyc.%d.sock", getpid());
    unlink(local_path);
    snprintf(local_path, sizeof(local_path), "/tmp/chronyc.%d.sock", getpid());
    unlink(local_path);
}

/* --- Public API --- */

int chronyctl_init(void) {
    pthread_mutex_lock(&chronyctl_mutex);
    chronyctl_initialized = 1;
    pthread_mutex_unlock(&chronyctl_mutex);
    return CHRONYCTL_SUCCESS;
}

int chronyctl_cleanup(void) {
    pthread_mutex_lock(&chronyctl_mutex);
    chronyctl_initialized = 0;
    pthread_mutex_unlock(&chronyctl_mutex);
    return CHRONYCTL_SUCCESS;
}

int chronyctl_get_offset(double *offset_sec) {
    if (!offset_sec) return CHRONYCTL_ERROR_INVALID;
    
    pthread_mutex_lock(&chronyctl_mutex);
    if (!chronyctl_initialized) { pthread_mutex_unlock(&chronyctl_mutex); return CHRONYCTL_ERROR_NOT_INIT; }
    
    int sockfd = connect_to_chronyd();
    if (sockfd < 0) { pthread_mutex_unlock(&chronyctl_mutex); return CHRONYCTL_ERROR_NO_DATA; }
    
    int ret = send_request(sockfd, REQ_TRACKING, NULL, 0);
    if (ret == 0) {
        RPY_Tracking tracking;
        ret = receive_reply(sockfd, RPY_TRACKING, &tracking, sizeof(tracking));
        if (ret == CHRONYCTL_SUCCESS) {
            *offset_sec = float_to_double(tracking.last_offset);
        }
    } else {
        ret = CHRONYCTL_ERROR_EXEC;
    }
    
    close(sockfd);
    cleanup_local_socket();
    pthread_mutex_unlock(&chronyctl_mutex);
    return ret;
}

int chronyctl_makestep(void) {
    pthread_mutex_lock(&chronyctl_mutex);
    if (!chronyctl_initialized) { pthread_mutex_unlock(&chronyctl_mutex); return CHRONYCTL_ERROR_NOT_INIT; }
    
    int sockfd = connect_to_chronyd();
    if (sockfd < 0) { pthread_mutex_unlock(&chronyctl_mutex); return CHRONYCTL_ERROR_NO_DATA; }
    
    int ret = send_request(sockfd, REQ_MAKESTEP, NULL, 0);
    if (ret == 0) {
        ret = receive_reply(sockfd, RPY_NULL, NULL, 0);
    } else {
        ret = CHRONYCTL_ERROR_EXEC;
    }
    
    close(sockfd);
    cleanup_local_socket();
    pthread_mutex_unlock(&chronyctl_mutex);
    return ret;
}

int chronyctl_add_server(const char *address, int minpoll, int maxpoll) {
    if (!address) return CHRONYCTL_ERROR_INVALID;
    
    pthread_mutex_lock(&chronyctl_mutex);
    if (!chronyctl_initialized) { pthread_mutex_unlock(&chronyctl_mutex); return CHRONYCTL_ERROR_NOT_INIT; }
    
    int sockfd = connect_to_chronyd();
    if (sockfd < 0) { pthread_mutex_unlock(&chronyctl_mutex); return CHRONYCTL_ERROR_NO_DATA; }
    
    REQ_NTP_Source payload;
    memset(&payload, 0, sizeof(payload));
    payload.type = htonl(REQ_ADDSRC_SERVER);
    strncpy((char *)payload.name, address, sizeof(payload.name) - 1);
    payload.name[sizeof(payload.name) - 1] = '\0';
    payload.port = htonl(123);
    payload.minpoll = htonl(minpoll);
    payload.maxpoll = htonl(maxpoll);
    payload.min_samples = htonl(6);
    payload.max_samples = htonl(12);
    payload.flags = htonl(REQ_ADDSRC_IBURST);
    
    int ret = send_request(sockfd, REQ_ADD_SOURCE, &payload, sizeof(payload));
    if (ret == 0) {
        ret = receive_reply(sockfd, RPY_NULL, NULL, 0);
    } else {
        ret = CHRONYCTL_ERROR_EXEC;
    }
    
    close(sockfd);
    cleanup_local_socket();
    pthread_mutex_unlock(&chronyctl_mutex);
    return ret;
}

int chronyctl_delete_server(const char *address) {
    if (!address) return CHRONYCTL_ERROR_INVALID;
    
    pthread_mutex_lock(&chronyctl_mutex);
    if (!chronyctl_initialized) { pthread_mutex_unlock(&chronyctl_mutex); return CHRONYCTL_ERROR_NOT_INIT; }
    
    int sockfd = connect_to_chronyd();
    if (sockfd < 0) { pthread_mutex_unlock(&chronyctl_mutex); return CHRONYCTL_ERROR_NO_DATA; }
    
    IPAddr host_ip;
    if (parse_address(address, &host_ip) != 0) {
        close(sockfd); cleanup_local_socket(); pthread_mutex_unlock(&chronyctl_mutex);
        return CHRONYCTL_ERROR_INVALID;
    }

    REQ_Del_Source payload;
    memset(&payload, 0, sizeof(payload));
    ip_host_to_network(&host_ip, &payload.ip_addr);
    
    int ret = send_request(sockfd, REQ_DEL_SOURCE, &payload, sizeof(payload));
    if (ret == 0) {
        ret = receive_reply(sockfd, RPY_NULL, NULL, 0);
    } else {
        ret = CHRONYCTL_ERROR_EXEC;
    }
    
    close(sockfd);
    cleanup_local_socket();
    pthread_mutex_unlock(&chronyctl_mutex);
    return ret;
}

int chronyctl_set_poll(const char *address, int minpoll, int maxpoll) {
    if (!address) return CHRONYCTL_ERROR_INVALID;
    
    pthread_mutex_lock(&chronyctl_mutex);
    if (!chronyctl_initialized) { pthread_mutex_unlock(&chronyctl_mutex); return CHRONYCTL_ERROR_NOT_INIT; }
    
    int sockfd = connect_to_chronyd();
    if (sockfd < 0) { pthread_mutex_unlock(&chronyctl_mutex); return CHRONYCTL_ERROR_NO_DATA; }
    
    IPAddr host_ip;
    if (parse_address(address, &host_ip) != 0) {
        close(sockfd); cleanup_local_socket(); pthread_mutex_unlock(&chronyctl_mutex);
        return CHRONYCTL_ERROR_INVALID;
    }
    
    IPAddr net_ip;
    ip_host_to_network(&host_ip, &net_ip);

    REQ_Modify_Minpoll min_payload = { .address = net_ip, .new_minpoll = htonl(minpoll) };
    int ret = send_request(sockfd, REQ_MODIFY_MINPOLL, &min_payload, sizeof(min_payload));
    if (ret == 0) ret = receive_reply(sockfd, RPY_NULL, NULL, 0);
    
    if (ret == CHRONYCTL_SUCCESS) {
        REQ_Modify_Maxpoll max_payload = { .address = net_ip, .new_maxpoll = htonl(maxpoll) };
        ret = send_request(sockfd, REQ_MODIFY_MAXPOLL, &max_payload, sizeof(max_payload));
        if (ret == 0) ret = receive_reply(sockfd, RPY_NULL, NULL, 0);
    }
    
    close(sockfd);
    cleanup_local_socket();
    pthread_mutex_unlock(&chronyctl_mutex);
    return ret;
}

const char* chronyctl_strerror(int err) {
    switch (err) {
        case CHRONYCTL_SUCCESS: return "Success";
        case CHRONYCTL_ERROR_INIT: return "Initialization error";
        case CHRONYCTL_ERROR_NOT_INIT: return "Library not initialized";
        case CHRONYCTL_ERROR_EXEC: return "Communication error with chronyd";
        case CHRONYCTL_ERROR_PARSE: return "Parse error";
        case CHRONYCTL_ERROR_INVALID: return "Invalid parameter";
        case CHRONYCTL_ERROR_MUTEX: return "Mutex error";
        case CHRONYCTL_ERROR_NO_DATA: return "Cannot reach chronyd";
        case CHRONYCTL_ERROR_UNAUTH: return "Permission denied (not root?)";
        default: return "Unknown error";
    }
}
