#!/bin/sh

# This script only installs the recurring expiry-check cron entry.
# The actual expiry decision happens in rdm -e by comparing current time with
# the stored absolute epoch in /nvram/rdm_debug_tool_expiry.

CRONTAB_DIR="${CRONTAB_DIR:-/var/spool/cron/crontabs}"
LEGACY_CRON_FILE="/var/spool/cron/root"
CRONFILE_BK="/tmp/rdmcron$$.txt"
LOGFILE="/opt/logs/rdm_debug_expiry.log"
CRON_LOGFILE="/opt/logs/rdm_status.log"

mkdir -p /var/spool/cron 2>/dev/null || true
mkdir -p /opt/logs 2>/dev/null || true

# Some devices use the legacy root cron file instead of /var/spool/cron/crontabs.
# Detect that layout and prefer the platform-native path instead of forcing one.
if [ -f "$LEGACY_CRON_FILE" ] || [ -d "/var/spool/cron" ] && [ ! -d "$CRONTAB_DIR" ]; then
    CRONTAB_DIR="/var/spool/cron"
fi

if [ -d "$CRONTAB_DIR" ]; then
    mkdir -p "$CRONTAB_DIR" 2>/dev/null || true
fi

# Copy existing cron entries if possible
if [ -f "$LEGACY_CRON_FILE" ]; then
    cp "$LEGACY_CRON_FILE" "$CRONFILE_BK" 2>/dev/null || : > "$CRONFILE_BK"
elif command -v crontab >/dev/null 2>&1; then
    if ! crontab -l -c "$CRONTAB_DIR" > "$CRONFILE_BK" 2>/dev/null; then
        if ! crontab -l > "$CRONFILE_BK" 2>/dev/null; then
            : > "$CRONFILE_BK"
        fi
    fi
else
    : > "$CRONFILE_BK"
fi

# Remove any old RDM expiry cron entry
sed -i '/\/usr\/bin\/rdm -e/d' "$CRONFILE_BK" 2>/dev/null || true

# Schedule a recurring minute-level check: actual expiry is checked in rdm -e.
ENTRY='* * * * * /usr/bin/rdm -e >> /opt/logs/rdm_status.log 2>&1'
echo "$ENTRY" >> "$CRONFILE_BK"

if [ -f "$LEGACY_CRON_FILE" ] || [ -d "/var/spool/cron" ] && [ ! -d "$CRONTAB_DIR" ]; then
    # Legacy cron layout: root cron is stored directly in /var/spool/cron/root.
    if [ -f "$LEGACY_CRON_FILE" ]; then
        sed -i '/\/usr\/bin\/rdm -e/d' "$CRONFILE_BK" 2>/dev/null || true
        printf '%s\n' "$ENTRY" >> "$LEGACY_CRON_FILE"
        chmod 0600 "$LEGACY_CRON_FILE" 2>/dev/null || true
        echo "RDM expiry cron scheduled every minute via legacy /var/spool/cron/root" >> "$LOGFILE"
        rm -f "$CRONFILE_BK"
        exit 0
    fi
fi

if command -v crontab >/dev/null 2>&1; then
    if crontab -c "$CRONTAB_DIR" "$CRONFILE_BK" 2>/dev/null; then
        echo "RDM expiry cron scheduled every minute via -c" >> "$LOGFILE"
        rm -f "$CRONFILE_BK"
        exit 0
    fi

    if crontab "$CRONFILE_BK" 2>/dev/null; then
        echo "RDM expiry cron scheduled every minute via default crontab" >> "$LOGFILE"
        rm -f "$CRONFILE_BK"
        exit 0
    fi
fi

if [ -d "/etc/cron.d" ]; then
    printf '%s\n' "$ENTRY" > "/etc/cron.d/rdm_debug_tool_expiry"
    chmod 0644 "/etc/cron.d/rdm_debug_tool_expiry"
    echo "RDM expiry cron scheduled every minute via /etc/cron.d" >> "$LOGFILE"
    rm -f "$CRONFILE_BK"
    exit 0
fi

echo "Failed to configure recurring RDM expiry cron" >> "$LOGFILE"
rm -f "$CRONFILE_BK"
exit 1
