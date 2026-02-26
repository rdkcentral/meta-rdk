#!/bin/sh

LOG_TAG="chrony-first-sync"
NTP_DIR="/tmp/systimemgr"
NTP_FILE="$NTP_DIR/ntp"
CLOCK_EVENT="/tmp/clock-event"
SYSTEMD_CLOCK="/var/lib/systemd/clock" #TBD -is this used?

log() {
    echo "$1"
}

milestone() {
    sh /lib/rdk/logMilestone.sh "$1"
}

is_synced() {
    chronyc tracking 2>/dev/null | grep -q "Leap status *: Normal"
}


# Wait for sync only if not already synced
#This command will wait (indefinitely, retrying every 0.1 seconds) until chrony reports that the system clock is synchronized
if is_synced; then
    log "Chrony already synchronised"
    exit 0       #TBD - Dont log milestones whenever chronyd started
 else
    log "Waiting for Chrony synchronisation..."
    chronyc waitsync 0 0.0 0.0 0.1 || {
        log "waitsync failed"
        exit 1
    }
fi

if [ ! -f "$CLOCK_EVENT" ]; then
touch "$CLOCK_EVENT" && log "Created $CLOCK_EVENT"
milestone "CLOCK_EVENT_CREATED"
fi

# Create required directory
if [ ! -d "$NTP_DIR" ]; then
    log "Creating $NTP_DIR"
    mkdir -p "$NTP_DIR"
fi

# Create flag files
if [ ! -f "$NTP_FILE" ]; then
touch "$NTP_FILE" && log "Created $NTP_FILE"
milestone "SYSTIMEMGR_NTP_CREATED"
fi

if [ ! -f "$SYSTEMD_CLOCK" ]; then
touch "$SYSTEMD_CLOCK" && log "Created $SYSTEMD_CLOCK"
milestone "SYSTEMD_CLOCK_CREATED"
fi

echo "Synchronized" > /tmp/ntp_status

exit 0
