#!/bin/sh

# This script only installs the recurring expiry-check cron entry.
# The actual expiry decision happens in rdm -e by comparing current time with
# the stored absolute epoch in /nvram/rdm_debug_tool_expiry.

CRON_HELPER="/lib/rdk/cronjobs_update.sh"
LOGFILE="/opt/logs/rdm_debug_expiry.log"
CRON_LOGFILE="/opt/logs/rdm_status.log"
ENTRY="* * * * * /usr/bin/rdm -e >> $CRON_LOGFILE 2>&1"

mkdir -p /opt/logs 2>/dev/null || true

# Prefer the platform helper used elsewhere in RDK for cron updates.
if [ -x "$CRON_HELPER" ]; then
    sh "$CRON_HELPER" "update" "rdm_debug_tool_expiry" "$ENTRY"
    echo "RDM expiry cron scheduled every minute via /lib/rdk/cronjobs_update.sh" >> "$LOGFILE"
    exit 0
fi

# Fallback: only one direct cron registration path.
CRONFILE_BK="/tmp/rdmcron$$.txt"

if [ -d "/etc/cron.d" ]; then
    printf '%s\n' "$ENTRY" > "/etc/cron.d/rdm_debug_tool_expiry"
    chmod 0644 "/etc/cron.d/rdm_debug_tool_expiry"
    echo "RDM expiry cron scheduled every minute via /etc/cron.d" >> "$LOGFILE"
    rm -f "$CRONFILE_BK"
    exit 0
fi

if [ -f "/var/spool/cron/root" ]; then
    printf '%s\n' "$ENTRY" >> "/var/spool/cron/root"
    chmod 0600 "/var/spool/cron/root" 2>/dev/null || true
    echo "RDM expiry cron scheduled every minute via /var/spool/cron/root" >> "$LOGFILE"
    rm -f "$CRONFILE_BK"
    exit 0
fi

echo "Failed to configure recurring RDM expiry cron" >> "$LOGFILE"
rm -f "$CRONFILE_BK"
exit 1
