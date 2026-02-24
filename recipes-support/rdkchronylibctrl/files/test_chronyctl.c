#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include "libchronyctl.h"

void check_err(int err, const char *msg) {
    if (err != CHRONYCTL_SUCCESS) {
        fprintf(stderr, "ERROR: %s: %s (%d)\n", msg, chronyctl_strerror(err), err);
    } else {
        printf("SUCCESS: %s\n", msg);
    }
}

void print_usage(const char *progname) {
    printf("Usage: %s <command> [args]\n", progname);
    printf("Commands:\n");
    printf("  makestep           - Force a time step\n");
    printf("  server [host] [min] [max] - Add a server (default: time.xfinity.com 6 10)\n");
    printf("  delete_server [host]- Delete a server (default: pool.ntp.org)\n");
    printf("  offset_check       - Get current offset\n");
}

int main(int argc, char *argv[]) {
    int ret;
    double offset;

    if (argc < 2) {
        print_usage(argv[0]);
        return 1;
    }

    const char *cmd = argv[1];

    printf("--- libchronyctl Test Application (%s) ---\n", cmd);

    ret = chronyctl_init();
    check_err(ret, "Initialization");
    if (ret != CHRONYCTL_SUCCESS) return 1;

    if (strcmp(cmd, "offset_check") == 0) {
        printf("\nTesting get_offset...\n");
        ret = chronyctl_get_offset(&offset);
        check_err(ret, "Get Offset");
        if (ret == CHRONYCTL_SUCCESS) {
            printf("Current Offset: %.9f seconds\n", offset);
        }
    } else if (strcmp(cmd, "makestep") == 0) {
        printf("\nTesting makestep...\n");
        ret = chronyctl_makestep();
        check_err(ret, "Make Step");
    } else if (strcmp(cmd, "server") == 0) {
        const char *host = (argc > 2) ? argv[2] : "time.xfinity.com";
        int minpoll = (argc > 3) ? atoi(argv[3]) : 6;
        int maxpoll = (argc > 4) ? atoi(argv[4]) : 10;
        printf("\nTesting add_server (%s, minpoll=%d, maxpoll=%d)...\n", host, minpoll, maxpoll);
        ret = chronyctl_add_server(host, minpoll, maxpoll);
        check_err(ret, "Add Server");
    } else if (strcmp(cmd, "delete_server") == 0) {
        const char *host = (argc > 2) ? argv[2] : "pool.ntp.org";
        printf("\nTesting delete_server (%s)...\n", host);
        ret = chronyctl_delete_server(host);
        check_err(ret, "Delete Server");
    } else {
        printf("Unknown command: %s\n", cmd);
        print_usage(argv[0]);
        chronyctl_cleanup();
        return 1;
    }

    chronyctl_cleanup();
    printf("\n--- Test Finished ---\n");

    return 0;
}
