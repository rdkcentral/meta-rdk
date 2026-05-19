/* File: ntp_sync_wait.c */

#include <stdio.h>
#include <string.h>
#include <unistd.h>
#include <sys/timex.h>
#include <fcntl.h>

#define POLL_INTERVAL_SEC 1
#define SYNC_FILE "/tmp/clock-event"

int main(void)
{
    struct timex tx;

    printf("chrony-sync-notify: waiting for NTP synchronization...\n");

    while (1) {
        memset(&tx, 0, sizeof(tx));

        if (adjtimex(&tx) < 0) {
            perror("adjtimex");

            /*
             * Negative/error scenario:
             * continue polling forever.
             */
            sleep(POLL_INTERVAL_SEC);
            continue;
        }

        /*
         * STA_UNSYNC set:
         * kernel clock is NOT synchronized.
         */
        if (tx.status & STA_UNSYNC) {
            printf("chrony-sync-notify: NTP not synchronized yet\n");
        } else {
            /*
             * NTP synchronization successful.
             */
            printf("chrony-sync-notify: NTP synchronized\n");
            touch("/tmp/systimemgr/ntp");
            touch("/tmp/clock-event");
            echo "ntp synchronised" > /tmp/ntp_status
            return 0;
        }

        sleep(POLL_INTERVAL_SEC);
    }

    return 0;
}
