#!/bin/bash
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
# Purpose : To generate the chrony configuration file  (/etc/rdk_chrony.conf)
# Scope : RDK Devices
# Usage : Invoke by systemd service as part of chrony integration


LOG_FILE="/opt/logs/chrony.log"
CHRONY_CONF=/etc/rdk_chrony.conf
DEFAULT_MINPOLL="10"
DEFAULT_MAXPOLL="12"

if [ -f /etc/env_setup.sh ]; then
    . /etc/env_setup.sh
fi

#create directory if not already available
/bin/mkdir -p /var/lib/chrony

#Log framework to print timestamp and source script name
ntpLog()
{
    echo "`/bin/timestamp` : $0: $*" >> "$LOG_FILE"
}

# -----------------------------------------------------------------------------
# Set system time based on LKG value or build time, if neither is available, log error
# -----------------------------------------------------------------------------
CLOCK_FILE="/opt/secure/clock.txt"
VERSION_FILE="/version.txt"

if [ -f "$CLOCK_FILE" ]; then
    TIME_VAL=$(cat "$CLOCK_FILE")
    if [[ "$TIME_VAL" =~ ^[0-9]+$ ]]; then
        HUMAN_DATE=$(date -d "@$TIME_VAL")
        ntpLog "Setting system time to LKG: $HUMAN_DATE (epoch $TIME_VAL)"
        date -s "@$TIME_VAL"
    else
        ntpLog "Invalid time value in $CLOCK_FILE"
    fi
elif [ -f "$VERSION_FILE" ]; then
    BUILD_TIME=$(grep '^BUILD_TIME=' "$VERSION_FILE" | cut -d= -f2- | tr -d '"')
    if [ -n "$BUILD_TIME" ]; then
        ntpLog "Setting system time to build time: $BUILD_TIME"
        date -s "$BUILD_TIME"
    else
        ntpLog "BUILD_TIME not found in $VERSION_FILE"
    fi
else
    ntpLog "Neither $CLOCK_FILE nor $VERSION_FILE found"
fi

# -----------------------------------------------------------------------------
# Fetch NTP server hostnames and poll intervals using property scripts
# -----------------------------------------------------------------------------
get_ntp_hosts() {
if [ -f /lib/rdk/getPartnerProperty.sh ]; then
     hostName=`/lib/rdk/getPartnerProperty.sh ntpHost`
     hostName2=`/lib/rdk/getPartnerProperty.sh ntpHost2`
     hostName3=`/lib/rdk/getPartnerProperty.sh ntpHost3`
     hostName4=`/lib/rdk/getPartnerProperty.sh ntpHost4`
     hostName5=`/lib/rdk/getPartnerProperty.sh ntpHost5`

     settings1=`/lib/rdk/getPartnerProperty.sh ntpHost1settings`
     settings2=`/lib/rdk/getPartnerProperty.sh ntpHost2settings`
     settings3=`/lib/rdk/getPartnerProperty.sh ntpHost3settings`
     settings4=`/lib/rdk/getPartnerProperty.sh ntpHost4settings`
     settings5=`/lib/rdk/getPartnerProperty.sh ntpHost5settings`

     maxStep=`/lib/rdk/getPartnerProperty.sh ntpMakestep`
fi
}

# Fallback: Fetch NTP hosts from bootstrap.ini if property script returns nothing
get_ntp_hosts_from_bootstrap() {
    BOOTSTRAP="/opt/secure/RFC/bootstrap.ini"

    if [ ! -f "$BOOTSTRAP" ]; then
        ntpLog "bootstrap.ini not found at $BOOTSTRAP"
        return 1
    fi

    # Helper to fetch key=value from bootstrap.ini (first match)
    get_bs_val() {
        key="$1"
        # Escape regex metacharacters in key so it is matched literally
        escaped_key=$(printf '%s\n' "$key" | sed 's/[][\\.^$*]/\\&/g')
        # Extract RHS after '=' and trim whitespace
        grep -m1 -E "^[[:space:]]*$escaped_key=" "$BOOTSTRAP" 2>/dev/null | \
            cut -d'=' -f2- | sed 's/^[[:space:]]*//; s/[[:space:]]*$//'
    }

    bs1="$(get_bs_val 'Device.Time.NTPServer1')"
    bs2="$(get_bs_val 'Device.Time.NTPServer2')"
    bs3="$(get_bs_val 'Device.Time.NTPServer3')"
    bs4="$(get_bs_val 'Device.Time.NTPServer4')"
    bs5="$(get_bs_val 'Device.Time.NTPServer5')"

    # Only fill missing values (don’t override TR-181 values if present)
    [ -z "$hostName" ]  && hostName="$bs1"
    [ -z "$hostName2" ] && hostName2="$bs2"
    [ -z "$hostName3" ] && hostName3="$bs3"
    [ -z "$hostName4" ] && hostName4="$bs4"
    [ -z "$hostName5" ] && hostName5="$bs5"

    return 0
}


ntpLog "Retrieve NTP Server URL from /lib/rdk/getPartnerProperty.sh..."
get_ntp_hosts

if ! ( [ "$hostName" ] || [ "$hostName2" ] || [ "$hostName3" ] || [ "$hostName4" ] || [ "$hostName5" ] ); then
    ntpLog "TR-181 returned empty NTP server list; falling back to /opt/secure/RFC/bootstrap.ini..."
    get_ntp_hosts_from_bootstrap
fi

hosts=("$hostName" "$hostName2" "$hostName3" "$hostName4" "$hostName5")
all_settings=("$settings1" "$settings2" "$settings3" "$settings4" "$settings5")
ntpLog "NTP Server URL for the partner:${hosts[*]}"

# If no partner URLs are available (even after bootstrap), keep the build-time default configuration as-is
if ! ( [ "$hostName" ] || [ "$hostName2" ] || [ "$hostName3" ] || [ "$hostName4" ] || [ "$hostName5" ] ); then
    ntpLog "No partner NTP URLs found; retaining build-time default configuration in $CHRONY_CONF"
    exit 0
fi

# Partner URLs are available — strip build-time default server entries before writing partner config
ntpLog "Partner NTP URLs found; removing build-time defaults from $CHRONY_CONF"
sed -i '/^[[:space:]]*server[[:space:]]\+global-bootstrap-time[12]\.xfinity\.com/d' "$CHRONY_CONF"

conf_written=0

# Add makestep directive to chrony config to control threshold/step correction
if [ -n "$maxstep" ]; then
    if echo "$maxstep" | grep -Eq '^[0-9]+(\.[0-9]+)?,[0-9]+$'; then
        stepval="${maxstep%%,*}"
        stepcount="${maxstep##*,}"
        echo "makestep $stepval $stepcount" >> "$CHRONY_CONF"
        ntpLog "Added makestep $stepval $stepcount to $CHRONY_CONF"
    else
        echo "makestep 
        1.0 3" >> "$CHRONY_CONF"
        ntpLog "NTPMaxstep value '$maxstep' is invalid, using default makestep 1.0 3 in $CHRONY_CONF"
    fi
else
    echo "makestep 1.0 3" >> "$CHRONY_CONF"
    ntpLog "NTPMaxstep is not set, using default makestep 1.0 3 in $CHRONY_CONF"
fi

# Parse a settings string "Type,MaxSources,Iburst,MinPoll,MaxPoll" into
# s_type, s_maxsources, s_iburst, s_minpoll, s_maxpoll.
# Falls back to defaults for any missing or invalid field.
parse_settings() {
    local raw="$1"
    s_type=$(echo "$raw"      | cut -d',' -f1)
    s_maxsources=$(echo "$raw" | cut -d',' -f2)
    s_iburst=$(echo "$raw"    | cut -d',' -f3)
    s_minpoll=$(echo "$raw"   | cut -d',' -f4)
    s_maxpoll=$(echo "$raw"   | cut -d',' -f5)

    # Normalise type: only "pool" is special, everything else is "server"
    [ "$s_type" = "pool" ] || s_type="server"

    # Validate poll values; fall back to defaults if non-numeric or inverted
    [[ "$s_minpoll" =~ ^[0-9]+$ ]] || s_minpoll="$DEFAULT_MINPOLL"
    [[ "$s_maxpoll" =~ ^[0-9]+$ ]] || s_maxpoll="$DEFAULT_MAXPOLL"
    if [ "$s_minpoll" -gt "$s_maxpoll" ]; then
        ntpLog "WARNING: minpoll ($s_minpoll) > maxpoll ($s_maxpoll) in settings '$raw', using defaults"
        s_minpoll="$DEFAULT_MINPOLL"
        s_maxpoll="$DEFAULT_MAXPOLL"
    fi

    # Normalise iburst: only literal "true" enables it
    [ "$s_iburst" = "true" ] || s_iburst="false"
}

# Add NTP servers ("server" or "pool" directive) to the configuration file

for i in $(seq 0 4); do
    host="${hosts[$i]}"
    raw="${all_settings[$i]}"
    if [ -n "$host" ]; then
        parse_settings "$raw"

        iburst_opt=""
        [ "$s_iburst" = "true" ] && iburst_opt=" iburst"

        if [ "$s_type" = "pool" ]; then
            maxsources_opt=""
            [[ "$s_maxsources" =~ ^[1-9][0-9]*$ ]] && maxsources_opt=" maxsources $s_maxsources"
            printf "pool %s%s%s minpoll %s maxpoll %s\n" \
                "$host" "$maxsources_opt" "$iburst_opt" "$s_minpoll" "$s_maxpoll" >> "$CHRONY_CONF"
        else
            printf "server %s%s minpoll %s maxpoll %s\n" \
                "$host" "$iburst_opt" "$s_minpoll" "$s_maxpoll" >> "$CHRONY_CONF"
        fi
        conf_written=1
    fi
done

# Remove duplicate NTP server entries, preserving only unique definitions

TMP_FILE="/tmp/rdk_chrony.deduped"
awk '
/^(server|pool)[ \t]+/ {
    if (!seen[$0]++) print
    next
}
{ print }
' "$CHRONY_CONF" > "$TMP_FILE"
cat "$TMP_FILE" > "$CHRONY_CONF"
rm -f "$TMP_FILE"

# Fallback block removed: if no partner URLs were found the script exits early
# above, preserving the build-time default configuration unchanged.

ntpLog "Successfully updated $CHRONY_CONF"

exit 0
