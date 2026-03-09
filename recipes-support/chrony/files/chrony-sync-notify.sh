#!/bin/sh

LOG_FILE=/opt/logs/chrony.log
NTP_DIR="/tmp/systimemgr"
NTP_FILE="$NTP_DIR/ntp"
CLOCK_EVENT="/tmp/clock-event"
SYSTEMD_DIR="/var/lib/systemd/"
SYSTEMD_CLOCK="$SYSTEMD_DIR/clock" 

log() {
    echo "$1" >> "$LOG_FILE"
}

milestone() {
    sh /lib/rdk/logMilestone.sh "$1"
}

is_synced() {
    chronyc tracking 2>/dev/null | grep -q "Leap status *: Normal"
}


# Wait for sync only if not already synced
#This command will wait up to about 5 minutes (3000 tries * 0.1 seconds each) until chrony reports that the system clock is synchronized
if is_synced; then
    log "Chrony already synchronised"
    exit 0       #TBD - Dont log milestones whenever chronyd started
 else
    log "Waiting for Chrony synchronisation..."
    chronyc waitsync 3000 0 0 0.1 || {
        log "waitsync failed or timeout after for 5 minutes"
        exit 1
    }
fi

if [ ! -f "$CLOCK_EVENT" ]; then
touch "$CLOCK_EVENT" && log "Created $CLOCK_EVENT"
fi

# Create required directory
if [ ! -d "$NTP_DIR" ]; then
    log "Creating $NTP_DIR"
    mkdir -p "$NTP_DIR"
fi

# Create flag files
if [ ! -f "$NTP_FILE" ]; then
touch "$NTP_FILE" && log "Created $NTP_FILE"
fi

if [ ! -d "$SYSTEMD_DIR" ]; then
    log "Creating $SYSTEMD_DIR"
    mkdir -p "$SYSTEMD_DIR"
fi

if [ ! -f "$SYSTEMD_CLOCK" ]; then
touch "$SYSTEMD_CLOCK" && log "Created $SYSTEMD_CLOCK"
fi

echo "Synchronized" > /tmp/ntp_status

exit 0
