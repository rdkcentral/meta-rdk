#include <stdio.h>
#include <unistd.h>
#include "time_driver.h"
#include <signal.h>

// Forward declaration of the driver
extern TimeDriver chrony_driver_socket;
TimeDriver *driver = NULL;

void handle_step_req()
{
    printf("Manally triggering steptime\n");
    driver->step_time();
}

void handle_offset_req()
{
    printf("This is a handler for offset req\n");
}

int main(int argc, char *argv[]) {
    setbuf(stdout, NULL);
    printf("Starting Time Manager Daemon (Socket Mode)...\n");
    driver = &chrony_driver_socket;

    signal(SIGUSR1, handle_offset_req);
    signal(SIGUSR2, handle_step_req);
    if (driver->init() != 0) {
        fprintf(stderr, "Failed to initialize driver %s\n", driver->name);
        return 1;
    }

    printf("Driver %s initialized successfully.\n", driver->name);

    // Main loop
    while (1) {
        double offset;
        if (driver->get_offset(&offset) == 0) {
            printf("[Daemon] Current Offset: %.9f seconds\n", offset);

            // Simple logic: if offset is huge, try to step
            if (offset > 1.0 || offset < -1.0) {
                printf("[Daemon] Offset too large, attempting to step time...\n");
                driver->step_time();
            }
        } else {
            fprintf(stderr, "[Daemon] Failed to get offset\n");
        }

        sleep(5);
    }

    driver->close();
    return 0;
}
