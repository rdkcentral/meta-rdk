#!/bin/sh
##############################################################################
# If not stated otherwise in this file or this component's LICENSE file the
# following copyright and licenses apply:
#
# Copyright 2020 RDK Management
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
##############################################################################
# Purpose : To Notify First synchronisation of chrony
# Scope : RDK Devices
# Usage : Invoke by systemd service as part of chrony integration

LOG_FILE=/opt/logs/chrony.log
NTP_DIR="/tmp/systimemgr"
NTP_FILE="$NTP_DIR/ntp"
CLOCK_EVENT="/tmp/clock-event"
SYSTEMD_DIR="/var/lib/systemd/"
SYSTEMD_CLOCK="$SYSTEMD_DIR/clock"

log() {
    echo "$(date -u +"%Y-%m-%dT%H:%M:%S.%3NZ") chronyd-sync-notify[$$]: $1" >> "$LOG_FILE"
}


is_synced() {
    chronyc tracking 2>/dev/null | grep -q "Leap status *: Normal"
}


# Wait for sync only if not already synced
#This command will wait until chrony reports that the system clock is synchronized
if is_synced; then
    log "Chrony already synchronised"
    exit 0       #TBD - Don't log milestones whenever chronyd started
 else
    log "Waiting for Chrony synchronisation..."
    chronyc waitsync 0 0 0 0.1 || {
        log "waitsync failed"
        exit 1
    }
fi

if [ ! -f "$CLOCK_EVENT" ]; then
touch "$CLOCK_EVENT" && log "Created $CLOCK_EVENT"
fi

if [ ! -d "$SYSTEMD_DIR" ]; then
    log "Creating $SYSTEMD_DIR"
    mkdir -p "$SYSTEMD_DIR"
fi

if [ ! -f "$SYSTEMD_CLOCK" ]; then
touch "$SYSTEMD_CLOCK" && log "Created $SYSTEMD_CLOCK"
fi

echo "Synchronized" > /tmp/ntp_status

if [ -d "$NTP_DIR" ]; then
   if touch "$NTP_FILE" && chmod 644 "$NTP_FILE"; then
      log "Created $NTP_FILE"
   else
      log "Failed to create or set permissions on $NTP_FILE"
      exit 1
   fi
else
   log "Directory $NTP_DIR does not exist; cannot create $NTP_FILE"
   exit 1
fi

exit 0
