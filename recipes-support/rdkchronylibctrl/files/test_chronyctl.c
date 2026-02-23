#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include "libchronyctl.h"

void check_err(int err, const char *msg) {
    if (err != CHRONYCTL_SUCCESS) {
        fprintf(stderr, "ERROR: %s: %s (%d)\n", msg, chronyctl_strerror(err), err);
        /* We don't exit here to try other APIs */
    } else {
        printf("SUCCESS: %s\n", msg);
    }
}

int main() {
    int ret;
    double offset;

    printf("--- libchronyctl Test Application ---\n");

    ret = chronyctl_init();
    check_err(ret, "Initialization");
    if (ret != CHRONYCTL_SUCCESS) return 1;

    printf("\n1. Testing get_offset...\n");
    ret = chronyctl_get_offset(&offset);
    check_err(ret, "Get Offset");
    if (ret == CHRONYCTL_SUCCESS) {
        printf("Current Offset: %.9f seconds\n", offset);
    }

    printf("\n2. Testing makestep...\n");
    ret = chronyctl_makestep();
    check_err(ret, "Make Step");

    printf("\n3. Testing add_server (pool.ntp.org)...\n");
    ret = chronyctl_add_server("time.xfinity.com", 6, 10);
    check_err(ret, "Add Server");

    printf("\n4. Testing set_poll (pool.ntp.org)...\n");
    ret = chronyctl_set_poll("pool.ntp.org", 7, 11);
    check_err(ret, "Set Poll");

    printf("\n5. Testing delete_server (pool.ntp.org)...\n");
    printf("Note: This might fail if the server address resolved differently than what was added.\n");
    /* Ideally we should resolve the address first, but for testing we'll try the name if the protocol allows, 
       or use a known IP. Chronyd often requires IP for deletion. */
    ret = chronyctl_delete_server("pool.ntp.org");
    check_err(ret, "Delete Server");

    chronyctl_cleanup();
    printf("\n--- Test Finished ---\n");

    return 0;
}
