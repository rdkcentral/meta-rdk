#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <sys/socket.h>
#include <sys/un.h>
#include <arpa/inet.h>
#include <math.h>
#include <time.h>
#include <errno.h>
#include <assert.h>

#include "time_driver.h"

// Bring in Chrony headers. 
// We assume they are in the parent directory.
#include "../candm.h"

#define SOCK_PATH_SERVER "/var/run/chrony/chronyd.sock"
#define SOCK_PATH_CLIENT_BASE "/var/run/chrony/time_daemon.sock"

static int sock_fd = -1;
static uint32_t sequence = 0;

// =======================================================================
// Floating point conversion logic copied from util.c / util.h
// =======================================================================

#define FLOAT_EXP_BITS 7
#define FLOAT_EXP_MIN (-(1 << (FLOAT_EXP_BITS - 1)))
#define FLOAT_EXP_MAX (-FLOAT_EXP_MIN - 1)
#define FLOAT_COEF_BITS ((int)sizeof (int32_t) * 8 - FLOAT_EXP_BITS)
#define FLOAT_COEF_MIN (-(1 << (FLOAT_COEF_BITS - 1)))
#define FLOAT_COEF_MAX (-FLOAT_COEF_MIN - 1)

static double UTI_FloatNetworkToHost(Float f) {
  int32_t exp, coef;
  uint32_t x;

  x = ntohl(f.f);

  exp = x >> FLOAT_COEF_BITS;
  if (exp >= 1 << (FLOAT_EXP_BITS - 1))
      exp -= 1 << FLOAT_EXP_BITS;
  exp -= FLOAT_COEF_BITS;

  coef = x % (1U << FLOAT_COEF_BITS);
  if (coef >= 1 << (FLOAT_COEF_BITS - 1))
      coef -= 1 << FLOAT_COEF_BITS;

  return coef * pow(2.0, exp);
}

// =======================================================================
// Driver Implementation
// =======================================================================

static int chrony_socket_init(void) {
    if (sock_fd >= 0) close(sock_fd);

    sock_fd = socket(AF_UNIX, SOCK_DGRAM, 0);
    if (sock_fd < 0) {
        perror("[ChronySocket] socket");
        return -1;
    }

    struct sockaddr_un client_addr;
    memset(&client_addr, 0, sizeof(client_addr));
    client_addr.sun_family = AF_UNIX;
    strncpy(client_addr.sun_path, SOCK_PATH_CLIENT_BASE, sizeof(client_addr.sun_path) - 1);

    // Unlink if exists
    unlink(SOCK_PATH_CLIENT_BASE);

    if (bind(sock_fd, (struct sockaddr *)&client_addr, sizeof(client_addr)) < 0) {
        perror("[ChronySocket] bind");
        close(sock_fd);
        sock_fd = -1;
        return -1;
    }

    // Connect to server to save us from typing the address every time
    struct sockaddr_un server_addr;
    memset(&server_addr, 0, sizeof(server_addr));
    server_addr.sun_family = AF_UNIX;
    strncpy(server_addr.sun_path, SOCK_PATH_SERVER, sizeof(server_addr.sun_path) - 1);

    if (connect(sock_fd, (struct sockaddr *)&server_addr, sizeof(server_addr)) < 0) {
        perror("[ChronySocket] connect");
        close(sock_fd);
        sock_fd = -1;
        return -1;
    }

    printf("[ChronySocket] Connected to %s via %s\n", SOCK_PATH_SERVER, SOCK_PATH_CLIENT_BASE);
    return 0;
}

static int send_request_and_get_reply(CMD_Request *req, CMD_Reply *reply) {
    if (sock_fd < 0) return -1;

    req->version = PROTO_VERSION_NUMBER;
    req->pkt_type = PKT_TYPE_CMD_REQUEST;
    req->res1 = 0;
    req->res2 = 0;
    req->attempt = 0;
    req->sequence = htonl(sequence++);
    // Padding is handled by struct size mostly, but good to zero it
    memset(req->padding, 0, sizeof(req->padding));

    if (send(sock_fd, req, sizeof(*req), 0) < 0) {
        perror("[ChronySocket] send");
        return -1;
    }

    // Wait for reply
    fd_set fds;
    FD_ZERO(&fds);
    FD_SET(sock_fd, &fds);
    struct timeval tv;
    tv.tv_sec = 2; // 2 second timeout
    tv.tv_usec = 0;

    int ret = select(sock_fd + 1, &fds, NULL, NULL, &tv);
    if (ret <= 0) {
        fprintf(stderr, "[ChronySocket] Timeout or error waiting for reply\n");
        return -1;
    }

    if (recv(sock_fd, reply, sizeof(*reply), 0) < 0) {
        perror("[ChronySocket] recv");
        return -1;
    }

    // Verify
    if (reply->version != PROTO_VERSION_NUMBER || 
        reply->pkt_type != PKT_TYPE_CMD_REPLY ||
        ntohl(reply->sequence) != (sequence - 1)) {
        fprintf(stderr, "[ChronySocket] Invalid reply packet\n");
        return -1;
    }

    if (ntohs(reply->status) != STT_SUCCESS) {
        fprintf(stderr, "[ChronySocket] Command failed with status %d\n", ntohs(reply->status));
        return -1;
    }

    return 0;
}

static int chrony_socket_step_time(void) {
    CMD_Request req;
    CMD_Reply reply;

    memset(&req, 0, sizeof(req));
    req.command = htons(REQ_MAKESTEP);

    // REQ_MAKESTEP (opcode 43) forces a step if run without args in chronyc
    // It doesn't require data payload for the "force" action in standard usage logic

    if (send_request_and_get_reply(&req, &reply) != 0) {
        return -1;
    }

    printf("[ChronySocket] Time stepped (command sent)\n");
    return 0;
}

static int chrony_socket_get_offset(double *offset_sec) {
    CMD_Request req;
    CMD_Reply reply;

    memset(&req, 0, sizeof(req));
    req.command = htons(REQ_TRACKING);

    if (send_request_and_get_reply(&req, &reply) != 0) {
        return -1;
    }

    // reply.data.tracking.last_offset
    *offset_sec = UTI_FloatNetworkToHost(reply.data.tracking.last_offset);
    return 0;
}

static int chrony_socket_add_server(const char *address) {
    (void)address;
    // Implementing ADD_SOURCE is complex due to the large struct.
    // For now we return error or log not implemented efficiently.
    fprintf(stderr, "[ChronySocket] add_server not fully implemented in this turbo driver\n");
    return -1;
}

static void chrony_socket_close(void) {
    if (sock_fd >= 0) {
        close(sock_fd);
        sock_fd = -1;
    }
    unlink(SOCK_PATH_CLIENT_BASE);
}

TimeDriver chrony_driver_socket = {
    .name = "chrony_socket",
    .init = chrony_socket_init,
    .step_time = chrony_socket_step_time,
    .get_offset = chrony_socket_get_offset,
    .add_server = chrony_socket_add_server,
    .close = chrony_socket_close
};
