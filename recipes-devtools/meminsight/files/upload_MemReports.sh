#!/bin/sh

# Where to upload (for demo: simply move to /var/log, adjust as needed)
UPLOAD_LOG="/var/log/meminsight_upload.log"
TRIGGER_FILE="/tmp/.meminsight_upload"

echo "[$(date)] MemInsight upload service triggered" >> "$UPLOAD_LOG"

if [ -f "$TRIGGER_FILE" ]; then
    echo "[$(date)] Uploading file: $TRIGGER_FILE" >> "$UPLOAD_LOG"
    cat "$TRIGGER_FILE" >> "$UPLOAD_LOG"
    rm -f "$TRIGGER_FILE"
    echo "[$(date)] File uploaded and trigger removed" >> "$UPLOAD_LOG"
else
    echo "[$(date)] Trigger file not found: $TRIGGER_FILE" >> "$UPLOAD_LOG"
fi
