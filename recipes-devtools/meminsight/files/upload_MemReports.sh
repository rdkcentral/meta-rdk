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

PATH=/usr/sbin:/usr/bin:/sbin:/bin

SCRIPT_NAME="upload_MemReports.sh"
CONFIGSTORE_PATH="/tmp/.meminsight_configstore"
UPLOAD_TRIGGER_PATH="/tmp/.meminsight_upload"
LOCK_DIR="/tmp/.meminsight_upload.lock"
LOG_TAG="[MemInsight-Upload]"
LOG_FILE="/opt/logs/meminsight.log"
RDK_LOGGER_PATH="/rdklogger"
MEMINSIGHT_INPROGRESS_FILE="/tmp/.meminsight_inprogress"

# Source RDK utilities: provides getWanInterfaceName (reads /etc/waninfo.sh internally)
if [ -f /lib/rdk/utils.sh ]; then
    . /lib/rdk/utils.sh
fi

# Source device properties: provides BOX_TYPE, WAN0_IS_DUMMY, UseLANIFIPV6 etc.
if [ -f /etc/device.properties ]; then
    . /etc/device.properties
fi

# mTLS client certificate injection (may redefine exec_curl_mtls or set cert env vars).
# Sourced once at startup — same pattern as uploadRDKBLogs.sh.
if [ -f /lib/rdk/exec_curl_mtls.sh ]; then
    . /lib/rdk/exec_curl_mtls.sh
fi

# OCSP stapling — set once at startup, same as uploadRDKBLogs.sh.
CERT_STATUS=""
if [ -f /tmp/.EnableOCSPStapling ] || [ -f /tmp/.EnableOCSPCA ]; then
    CERT_STATUS="--cert-status"
fi

# Default values (overridden from configstore)
RUN_ID=""
UPLOAD_ENABLED=false
UPLOAD_INTERVAL=0
OUTPUT_DIR="/tmp/meminsight/"
STAGING_DIR=""

##############################################################################
# Utilities
##############################################################################

# Log a message with the standard tag prefix.
# Parameters: $* - message text
log() {
    echo "$LOG_TAG $*" >> "$LOG_FILE"	
}

# Execute a command at lowest I/O and CPU priority.
# Tests ionice -c3 support first; falls back to nice-only if unsupported.
# The command is never executed twice — ionice capability is tested separately.
# Parameters: $* - command and arguments to run
# Returns: exit code of the executed command
run_low_priority() {
    if ionice -c3 true >/dev/null 2>&1; then
        ionice -c3 nice -n 19 "$@"
    else
        nice -n 19 "$@"
    fi
}

##############################################################################
# Lock management
##############################################################################

# Remove the lock directory created by acquire_lock().
# Registered via trap — runs automatically on EXIT, INT, or TERM.
cleanup_lock() {
    rmdir "$LOCK_DIR" >/dev/null 2>&1 || true
}

# Remove the upload trigger file so the systemd path unit does not
# immediately re-trigger the service after a graceful exit.
cleanup_upload_trigger() {
    rm -f "$UPLOAD_TRIGGER_PATH" >/dev/null 2>&1 || true
}

# Acquire an exclusive instance lock via atomic mkdir.
# Registers cleanup_lock via trap so the lock is always released on exit.
# Exits with code 0 (not an error) if another instance already holds the lock.
acquire_lock() {
    if ! mkdir "$LOCK_DIR" >/dev/null 2>&1; then
        log "Another upload instance is already running; exiting."
        exit 0
    fi
    trap cleanup_lock EXIT INT TERM
}

##############################################################################
# Device identity
##############################################################################

# Return the MAC address of the primary network interface, uppercase, no colons.
# Tries getMacAddressOnly first, then /sys/class/net, then returns "UNKNOWNMAC".
get_mac_address() {
    if command -v getMacAddressOnly >/dev/null 2>&1; then
        getMacAddressOnly 2>/dev/null | tr -d ':' | tr '[:lower:]' '[:upper:]'
        return
    fi
    for f in /sys/class/net/*/address; do
        [ -f "$f" ] || continue
        case "$f" in */lo/*) continue ;; esac
        mac="$(cat "$f" 2>/dev/null | tr -d ':' | tr '[:lower:]' '[:upper:]')"
        [ -n "$mac" ] && echo "$mac" && return
    done
    echo "UNKNOWNMAC"
}

##############################################################################
# Configuration
##############################################################################

# Parse CONFIGSTORE_PATH and populate global variables.
# Applies built-in defaults first so the script works even if the file is absent.
# Sets globals: RUN_ID, UPLOAD_ENABLED, UPLOAD_INTERVAL, OUTPUT_DIR
# Returns: 0 on success, 1 if configstore file not found (defaults applied)
load_configstore() {
    RUN_ID=""
    UPLOAD_ENABLED=false
    UPLOAD_INTERVAL=0
    OUTPUT_DIR="/tmp/meminsight/"

    if [ ! -f "$CONFIGSTORE_PATH" ]; then
        log "Configstore not found at $CONFIGSTORE_PATH; using built-in defaults."
        return 1
    fi

    while IFS='=' read -r key value; do
        case "$key" in \#*|'') continue ;; esac
        case "$key" in
            RUN_ID)          RUN_ID="$value" ;;
            UPLOAD_ENABLED)  [ "$value" = "1" ] && UPLOAD_ENABLED=true || UPLOAD_ENABLED=false ;;
            UPLOAD_INTERVAL) UPLOAD_INTERVAL="$value" ;;
            OUTPUT_DIR)      OUTPUT_DIR="$value" ;;
        esac
    done < "$CONFIGSTORE_PATH"

    log "Configstore loaded: RUN_ID=$RUN_ID UPLOAD_ENABLED=$UPLOAD_ENABLED UPLOAD_INTERVAL=${UPLOAD_INTERVAL}s OUTPUT_DIR=$OUTPUT_DIR"
    return 0
}

##############################################################################
# Run-state helpers
##############################################################################

# Check whether the meminsight process is currently running.
# Used to decide whether the upload loop should exit after this cycle.
# Returns: 0 (true) if meminsight process found, 1 (false) otherwise
is_meminsight_running() {
    pidof meminsight >/dev/null 2>&1
}

# Check whether a meminsight capture iteration is currently being written.
# Relies on the .meminsight_inprogress marker created at run start and
# removed just before the last iteration file is closed.
# Used exclusively to decide which iteration to exclude from the upload set.
# Returns: 0 (true) if marker exists, 1 (false) otherwise
is_capture_in_progress() {
    [ -f "$MEMINSIGHT_INPROGRESS_FILE" ]
}

# Return the highest iteration number present in OUTPUT_DIR.
# Used to identify which iter files may still be open for writing.
# Returns: integer >= 0, or -1 if no iteration files are found
get_highest_iteration() {
    max_iter=-1
    for f in $(find "$OUTPUT_DIR" -maxdepth 1 -type f \( -name '*.csv' -o -name '*.json' \) 2>/dev/null); do
        bname="$(basename "$f")"
        iter="$(echo "$bname" | sed -n 's/.*_iter\([0-9][0-9]*\)_.*/\1/p')"
        [ -z "$iter" ] && iter="$(echo "$bname" | sed -n 's/.*_iter\([0-9][0-9]*\)\..*/\1/p')"
        if [ -n "$iter" ] && [ "$iter" -gt "$max_iter" ]; then
            max_iter="$iter"
        fi
    done
    echo "$max_iter"
}

# Build the set of report files eligible for upload in this cycle.
#
# If /tmp/.meminsight_inprogress exists (active write in progress):
#   All .csv/.json in OUTPUT_DIR EXCEPT files belonging to the highest
#   iteration number (which may still be open for writing).
#
# If /tmp/.meminsight_inprogress is absent (no active write):
#   All .csv/.json in OUTPUT_DIR with no exclusions (full drain).
#
# Returns: newline-separated list of absolute paths, or empty string
build_upload_set() {
    all_files="$(find "$OUTPUT_DIR" -maxdepth 1 -type f \( -name '*.csv' -o -name '*.json' \) 2>/dev/null)"
    [ -z "$all_files" ] && return 0

    if is_capture_in_progress; then
        max_iter="$(get_highest_iteration)"
        for f in $all_files; do
            bname="$(basename "$f")"
            iter="$(echo "$bname" | sed -n 's/.*_iter\([0-9][0-9]*\)_.*/\1/p')"
            [ -z "$iter" ] && iter="$(echo "$bname" | sed -n 's/.*_iter\([0-9][0-9]*\)\..*/\1/p')"
            if [ -n "$iter" ] && [ "$iter" -eq "$max_iter" ]; then
                log "Skipping active iter${max_iter}: $(basename "$f")"
                continue
            fi
            echo "$f"
        done
    else
        echo "$all_files"
    fi
}

##############################################################################
# Upload configuration
##############################################################################

# Resolve MemInsight upload endpoint using the same precedence as memcapture:
#   1. TR-181 LogServerUrl
#   2. UploadRepository URL from OUTFILE
#   3. TR-181 SsrUrl + /cgi-bin/S3.cgi
#   4. dcm.properties fallback for LOG_SERVER / HTTP_UPLOAD_LINK
# Sets globals: LOG_SERVER, HTTP_UPLOAD_LINK, UPLOAD_PROTOCOL
resolve_upload_config() {
    LOG_SERVER=""
    HTTP_UPLOAD_LINK=""
    UPLOAD_PROTOCOL="HTTP"
    OUTFILE="/tmp/DCMSettings.conf"

    if [ "$BUILD_TYPE" != "prod" ] && [ -f /opt/dcm.properties ]; then
        log "Configurable service end-points will not be used for $BUILD_TYPE builds due to overridden /opt/dcm.properties."
    else
        if command -v tr181 >/dev/null 2>&1; then
            LOG_SERVER="$(tr181 -g Device.DeviceInfo.X_RDKCENTRAL-COM_RFC.LogUpload.LogServerUrl 2>/dev/null)"

            if [ -f "$OUTFILE" ]; then
                HTTP_UPLOAD_LINK="$(grep 'LogUploadSettings:UploadRepository:URL' "$OUTFILE" | cut -d '=' -f2 | sed 's/^"//' | sed 's/"$//')"
                UPLOAD_PROTOCOL="$(grep 'LogUploadSettings:UploadRepository:uploadProtocol' "$OUTFILE" | cut -d '=' -f2 | sed 's/^"//' | sed 's/"$//')"
            fi

            if [ -z "$HTTP_UPLOAD_LINK" ]; then
                UPLOAD_HTTPLINK_URL="$(tr181 -g Device.DeviceInfo.X_RDKCENTRAL-COM_RFC.LogUpload.SsrUrl 2>/dev/null)"
                if [ -n "$UPLOAD_HTTPLINK_URL" ]; then
                    HTTP_UPLOAD_LINK="${UPLOAD_HTTPLINK_URL}/cgi-bin/S3.cgi"
                fi
            fi
        fi
    fi

    [ -n "$UPLOAD_PROTOCOL" ] || UPLOAD_PROTOCOL="HTTP"

    if [ -z "$LOG_SERVER" ] || [ -z "$HTTP_UPLOAD_LINK" ]; then
        if [ "$BUILD_TYPE" != "prod" ] && [ -f /opt/dcm.properties ]; then
            # shellcheck disable=SC1091
            . /opt/dcm.properties
        elif [ -f /etc/dcm.properties ]; then
            # shellcheck disable=SC1091
            . /etc/dcm.properties
        fi

        [ -z "$LOG_SERVER" ] && LOG_SERVER="${LOG_SERVER_URL:-$LOG_SERVER}"
        [ -z "$HTTP_UPLOAD_LINK" ] && HTTP_UPLOAD_LINK="${HTTP_UPLOAD_LINK:-${UPLOAD_HTTPLINK_URL:+${UPLOAD_HTTPLINK_URL}/cgi-bin/S3.cgi}}"
    fi

    log "Resolved upload config: LOG_SERVER=${LOG_SERVER:-<empty>} HTTP_UPLOAD_LINK=${HTTP_UPLOAD_LINK:-<empty>} UPLOAD_PROTOCOL=${UPLOAD_PROTOCOL:-HTTP}"
}

##############################################################################
# Upload
##############################################################################

# Upload archive using logupload, similar to memcapture flow.
# Parameters:
#   $1 - archive path
# Returns:
#   0 on success, 1 on failure
perform_logupload() {
    archive_file="$1"

    resolve_upload_config

    if [ -z "$LOG_SERVER" ] || [ -z "$HTTP_UPLOAD_LINK" ]; then
        log "Upload configuration incomplete: LOG_SERVER=${LOG_SERVER:-<empty>} HTTP_UPLOAD_LINK=${HTTP_UPLOAD_LINK:-<empty>}"
        return 1
    fi

    if [ ! -f "$archive_file" ]; then
        log "Archive file not found: $archive_file"
        return 1
    fi

    log "Invoking /usr/bin/logupload for $(basename "$archive_file")"
    log "/usr/bin/logupload \"$LOG_SERVER\" 1 1 false \"$UPLOAD_PROTOCOL\" \"$HTTP_UPLOAD_LINK\" \"MEMINSIGHT\" true \"$archive_file\""

    /usr/bin/logupload "$LOG_SERVER" 1 1 false "$UPLOAD_PROTOCOL" "$HTTP_UPLOAD_LINK" "MEMINSIGHT" true "$archive_file"
    retval=$?

    if [ "$retval" -ne 0 ]; then
        log "MemInsight report upload failed with code $retval"
        return 1
    fi

    log "MemInsight report upload succeeded"
    return 0
}

##############################################################################
# Per-cycle orchestration
##############################################################################

# Execute one upload cycle.
#
# RUNNING MODE (meminsight process alive — pidof meminsight succeeds):
#   1. Check OUTPUT_DIR exists; if not, log and return 0 (retry next cycle).
#   2. Build upload set — skip files belonging to highest iteration if
#      /tmp/.meminsight_inprogress marker is present (active write guard).
#   3. If nothing eligible → log and return 0 (sleep and retry).
#   4. mkdir -p STAGING_DIR.
#   5. tar eligible files from OUTPUT_DIR into STAGING_DIR/archive.tar.gz.
#      tar failure → rm partial archive, source files stay, return 0 (retry).
#   6. Upload archive.
#      Success → rm archive from STAGING_DIR + rm uploaded source files from OUTPUT_DIR.
#      Failure → rm archive from STAGING_DIR only; source files stay for next retry.
#   7. Always return 0 → main loop sleeps and retries.
#
# DRAIN MODE (meminsight process gone — pidof meminsight fails):
#   1. Check OUTPUT_DIR exists; if not, return 1 (exit).
#   2. Build upload set — no exclusions (full drain).
#   3. If nothing present → log "nothing to do" and return 1 (exit).
#   4. mkdir -p STAGING_DIR.
#   5. tar all eligible files into STAGING_DIR/archive.tar.gz.
#      tar failure → rm partial archive, log leftover filenames, return 1 (exit).
#   6. Upload archive.
#      Success → rm archive + rm uploaded source files, return 1 (exit cleanly).
#      Failure → rm archive only, log leftover filenames, return 1 (exit).
#   7. Always return 1 → main loop exits after this cycle.
#
# Return values:
#   0 = meminsight still running, keep looping
#   1 = meminsight not running, exit loop after this cycle
upload_cycle() {
    meminsight_running=false
    is_meminsight_running && meminsight_running=true

    # ── Check output directory ────────────────────────────────────────────────
    if [ ! -d "$OUTPUT_DIR" ]; then
        log "Output directory $OUTPUT_DIR not found; skipping cycle."
        $meminsight_running && return 0 || return 1
    fi

    # ── Build eligible file set ───────────────────────────────────────────────
    selected_files="$(build_upload_set)"

    if [ -z "$selected_files" ]; then
        if $meminsight_running; then
            log "No eligible reports yet (highest iteration excluded or none present); will retry after sleep."
            return 0
        else
            log "No reports found in $OUTPUT_DIR; nothing to upload."
            return 1
        fi
    fi

    # ── Ensure staging directory exists ──────────────────────────────────────
    if ! mkdir -p "$STAGING_DIR" 2>/dev/null; then
        log "ERROR: Cannot create staging directory $STAGING_DIR"
        $meminsight_running && return 0 || return 1
    fi

    # ── Create archive in STAGING_DIR; source files remain in OUTPUT_DIR ─────
    # Archive name encodes MAC, UTC timestamp, and RUN_ID for traceability.
    mac="$(get_mac_address)"
    timestamp="$(date -u +%Y%m%d-%H%M%S)"
    archive_path="${STAGING_DIR}/${mac}_${timestamp}_${RUN_ID}_meminsight.tgz"

    # Collect basenames; cd to OUTPUT_DIR so archive paths are clean (no leading dirs).
    rel_files=""
    for f in $selected_files; do
        rel_files="$rel_files $(basename "$f")"
    done

    if ! (cd "$OUTPUT_DIR" && run_low_priority tar -czf "$archive_path" $rel_files); then
        log "tar failed; removing partial archive. Source reports untouched in $OUTPUT_DIR."
        rm -f "$archive_path"
        if $meminsight_running; then
            log "Will retry on next cycle."
            return 0
        else
            log "Leftover reports (not uploaded):$selected_files"
            return 1
        fi
    fi

    log "Archive created: $(basename "$archive_path") ($(du -sh "$archive_path" 2>/dev/null | awk '{print $1}'))"

    # ── Upload ────────────────────────────────────────────────────────────────
    if perform_logupload "$archive_path"; then
        rm -f "$archive_path"
        for f in $selected_files; do rm -f "$f"; done
        log "Upload successful; archive and source reports removed from $OUTPUT_DIR."
    else
        rm -f "$archive_path"
        log "Upload failed; archive removed. Source reports retained in $OUTPUT_DIR for retry."
        if ! $meminsight_running; then
            log "Leftover reports (not uploaded):$selected_files"
        fi
    fi

    $meminsight_running && return 0 || return 1
}

##############################################################################
# Entry point
##############################################################################

# Compute an MD5 hash of the configstore file for change detection.
# Returns: hex digest string, or empty string if the file is absent/unreadable.
configstore_hash() {
    md5sum "$CONFIGSTORE_PATH" 2>/dev/null | awk '{print $1}'
}

# Main control loop.
#
# Flow:
#   acquire lock → load config → snapshot hash → validate → loop {
#       sleep → check hash → reload if changed → upload_cycle } → exit
#
# Configstore hash check: after each sleep, md5sum of CONFIGSTORE_PATH is
# compared to the value snapshotted at the previous load.  If meminsight
# restarted (new RUN_ID, different OUTPUT_DIR, changed interval) the hash
# will differ and load_configstore() is called again before the upload cycle.
#
# The loop continues as long as upload_cycle returns 0 (meminsight is running).
# When upload_cycle returns 1 (meminsight has exited), the loop breaks and the
# script exits cleanly, removing the upload trigger file to prevent the systemd
# path unit from immediately re-triggering.
main() {
    acquire_lock
    load_configstore

    if ! $UPLOAD_ENABLED; then
        log "Upload is not enabled in configstore; exiting."
        cleanup_upload_trigger
        exit 0
    fi

    if [ "$UPLOAD_INTERVAL" -eq 0 ]; then
        UPLOAD_INTERVAL=900
        log "No upload interval configured; defaulting to 900 seconds."
    fi

    # Strip trailing slash before appending _staging suffix
    STAGING_DIR="${OUTPUT_DIR%/}_staging"

    # Snapshot configstore hash so we can detect if meminsight restarts and
    # writes a new RUN_ID (or changed OUTPUT_DIR / interval) while we sleep.
    last_hash="$(configstore_hash)"

    log "Upload service started: interval=${UPLOAD_INTERVAL}s output=$OUTPUT_DIR staging=$STAGING_DIR hash=$last_hash"

    while true; do
        log "Sleeping ${UPLOAD_INTERVAL}s until next upload cycle..."
        sleep "$UPLOAD_INTERVAL"

        # ── Configstore change detection ──────────────────────────────────────
        # Meminsight may have crashed and restarted with a new RUN_ID, a
        # different OUTPUT_DIR, or a different interval since we last slept.
        # Reload whenever the file content has changed.
        current_hash="$(configstore_hash)"
        if [ -n "$current_hash" ] && [ "$current_hash" != "$last_hash" ]; then
            log "Configstore changed (hash $last_hash → $current_hash); reloading."
            load_configstore
            last_hash="$current_hash"
            # Recompute derived values that depend on OUTPUT_DIR / UPLOAD_INTERVAL.
            [ "$UPLOAD_INTERVAL" -eq 0 ] && UPLOAD_INTERVAL=900
            STAGING_DIR="${OUTPUT_DIR%/}_staging"
            log "Config reloaded: interval=${UPLOAD_INTERVAL}s output=$OUTPUT_DIR staging=$STAGING_DIR"
        fi

        upload_cycle || break
    done

    # ── Exit phase ────────────────────────────────────────────────────────────
    log "Upload service exiting."
    if [ -n "$STAGING_DIR" ] && [ -d "$STAGING_DIR" ]; then
        rm -f "$STAGING_DIR"/*.tgz 2>/dev/null
        rmdir "$STAGING_DIR" >/dev/null 2>&1 || true
    fi
    cleanup_upload_trigger

    exit 0
}

main "$@"
