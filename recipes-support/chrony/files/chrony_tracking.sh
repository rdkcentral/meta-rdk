#!/bin/sh
LOGFILE="/opt/logs/chrony.log"

timestamp=$(date -u +"%Y-%m-%dT%H:%M:%S.%3NZ")

{
echo "$timestamp chrony telemetry:"
chronyc tracking
echo ""
} >> "$LOGFILE"
