#!/bin/sh
##########################################################################
# If not stated otherwise in this file or this component's LICENSE
# file the following copyright and licenses apply:
#
# Copyright 2025 RDK Management
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
##########################################################################

SRC_BIN="/tmp/meminsight/usr/bin/meminsight"
DST_BIN="/run/meminsight/usr/bin/meminsight"
RFC_PARAM="Device.DeviceInfo.X_RDKCENTRAL-COM_RFC.Feature.meminsight.Trigger"

# Load DEVICE_TYPE
if [ -f /etc/device.properties ]; then
    . /etc/device.properties
fi

if [ -z "$DEVICE_TYPE" ]; then
    DEVICE_TYPE="unknown"
fi

case "$DEVICE_TYPE" in
    mediaclient)
        RDM_LOG_FILE="/opt/logs/rdm-status.log"
        ;;
    broadband)
        RDM_LOG_FILE="/rdklogs/logs/rdm-status.log.0"
        ;;
    *)
        RDM_LOG_FILE="/var/log/rdm-status.log"
        ;;
esac

log_info() {
    echo "[start_meminsight] [INFO] $*" >> $RDM_LOG_FILE
}

log_error() {
    echo "[start_meminsight] [ERROR] $*" >> $RDM_LOG_FILE
}

log_info "Starting meminsight post-service script"
log_info "Configured source binary: $SRC_BIN"
log_info "Configured destination binary: $DST_BIN"
log_info "Configured RFC parameter: $RFC_PARAM"

if [ -z "$SRC_BIN" ] || [ ! -f "$SRC_BIN" ]; then
    log_error "Invalid meminsight source binary: $SRC_BIN"
    exit 1
fi

log_info "Creating destination directory: $(dirname "$DST_BIN")"
if ! mkdir -p "$(dirname "$DST_BIN")"; then
    log_error "Failed to create destination directory"
    exit 1
fi

log_info "Copying binary from $SRC_BIN to $DST_BIN"
if ! cp "$SRC_BIN" "$DST_BIN"; then
    log_error "Failed to copy binary"
    exit 1
fi

log_info "Setting executable permission on $DST_BIN"
if ! chmod +x "$DST_BIN"; then
    log_error "Failed to set executable permission on $DST_BIN"
    exit 1
fi
log_info "Binary copy and permission update completed"

RFC_TRIGGER_VALUE=""
if command -v dmcli >/dev/null 2>&1; then
    log_info "Reading RFC trigger value via dmcli"
    RFC_TRIGGER_VALUE="$(dmcli eRT getv "$RFC_PARAM" 2>/dev/null | awk -F'value: ' '/value:/ {print $2; exit}' | tr -d '\r' | xargs)"
    log_info "RFC trigger value: ${RFC_TRIGGER_VALUE:-<empty>}"
else
    log_info "dmcli not available; RFC trigger value remains empty"
fi

if [ "$RFC_TRIGGER_VALUE" = "start" ]; then
    log_info "RFC trigger is start; creating /tmp/.enable_meminsight"
    touch /tmp/.enable_meminsight
    log_info "Created /tmp/.enable_meminsight"
else
    log_info "RFC trigger is not start; skipping /tmp/.enable_meminsight creation"
fi

log_info "Meminsight post-service script completed"
