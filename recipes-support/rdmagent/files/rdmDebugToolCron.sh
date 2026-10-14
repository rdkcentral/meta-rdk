#!/bin/sh

# Usage: rdmDebugToolCron.sh <minute> <hour>

MIN="$1"
HOUR="$2"

CRONTAB_DIR="${CRONTAB_DIR:-/var/spool/cron/crontabs}"
CRONFILE_BK="/tmp/rdmcron$$.txt"
LOGFILE="/opt/logs/rdm_debug_expiry.log"
CRON_LOGFILE="/opt/logs/rdm_status.log"

mkdir -p /var/spool/cron 2>/dev/null || true

if [ ! -d "$CRONTAB_DIR" ]; then
    if [ -d "/var/spool/cron/crontabs" ]; then
        CRONTAB_DIR="/var/spool/cron/crontabs"
    else
        CRONTAB_DIR="/var/spool/cron"
    fi
fi

mkdir -p "$CRONTAB_DIR" 2>/dev/null || true

if [ -z "$MIN" ] || [ -z "$HOUR" ]; then
    echo "Invalid cron time: MIN=$MIN HOUR=$HOUR" >> "$LOGFILE"
    exit 1
fi

# Copy existing cron entries
if ! crontab -l -c "$CRONTAB_DIR" > "$CRONFILE_BK" 2>/dev/null; then
    if ! crontab -l > "$CRONFILE_BK" 2>/dev/null; then
        : > "$CRONFILE_BK"
    fi
fi

# Remove existing RDM expiry cron entry
sed -i '/\/usr\/bin\/rdm -e/d' "$CRONFILE_BK" 2>/dev/null || true

# Add the new RDM expiry cron entry
echo "$MIN $HOUR * * * /usr/bin/rdm -e >> $CRON_LOGFILE 2>&1" >> "$CRONFILE_BK"

# Update crontab
if crontab -c "$CRONTAB_DIR" "$CRONFILE_BK" 2>/dev/null; then
    echo "RDM expiry cron scheduled at ${HOUR}:${MIN}" >> "$LOGFILE"
    rm -f "$CRONFILE_BK"
    exit 0
fi

if crontab "$CRONFILE_BK" 2>/dev/null; then
    echo "RDM expiry cron scheduled at ${HOUR}:${MIN} via default crontab" >> "$LOGFILE"
    rm -f "$CRONFILE_BK"
    exit 0
fi

echo "Failed to configure RDM expiry cron" >> "$LOGFILE"
rm -f "$CRONFILE_BK"
exit 1
