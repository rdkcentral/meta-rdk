#!/bin/sh

# Usage: rdmDebugToolCron.sh <minute> <hour>

MIN="$1"
HOUR="$2"

CRONTAB_DIR="/var/spool/cron/crontabs/"
CRONFILE_BK="/tmp/rdmcron$$.txt"
LOGFILE="/opt/logs/rdm_debug_expiry.log"
CRON_LOGFILE="/opt/logs/rdm_status.log"

if [ -z "$MIN" ] || [ -z "$HOUR" ]; then
    echo "Invalid cron time: MIN=$MIN HOUR=$HOUR" >> "$LOGFILE"
    exit 1
fi

# Copy existing cron entries
if ! crontab -l -c "$CRONTAB_DIR" > "$CRONFILE_BK" 2>/dev/null; then
    : > "$CRONFILE_BK"
fi

# Remove existing RDM expiry cron entry
sed -i '/\/usr\/bin\/rdm -e/d' "$CRONFILE_BK"

# Add the new RDM expiry cron entry
echo "$MIN $HOUR * * * /usr/bin/rdm -e >> $CRON_LOGFILE 2>&1" >> "$CRONFILE_BK"

# Update crontab
if crontab "$CRONFILE_BK" -c "$CRONTAB_DIR"; then
    echo "RDM expiry cron scheduled at ${HOUR}:${MIN}" >> "$LOGFILE"
    rm -f "$CRONFILE_BK"
    exit 0
fi

echo "Failed to configure RDM expiry cron" >> "$LOGFILE"
rm -f "$CRONFILE_BK"
exit 1
