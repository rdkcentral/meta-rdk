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
attempts=1
max_attempts=5
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
     
     minPoll=`/lib/rdk/getPartnerProperty.sh NTPMinpoll`
     maxPoll=`/lib/rdk/getPartnerProperty.sh NTPMaxpoll`

   # Fetch directives for each NTP server (optional, fallback to server)
     directive1=$( /lib/rdk/getPartnerProperty.sh NTPServer1Directive )
     directive2=$( /lib/rdk/getPartnerProperty.sh NTPServer2Directive )
     directive3=$( /lib/rdk/getPartnerProperty.sh NTPServer3Directive )
     directive4=$( /lib/rdk/getPartnerProperty.sh NTPServer4Directive )
     directive5=$( /lib/rdk/getPartnerProperty.sh NTPServer5Directive )

     maxstep=`/lib/rdk/getPartnerProperty.sh NTPMaxstep`
  
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
while [ "$attempts" -le "$max_attempts" ]; do

    ntpLog "Attempt $attempts/$max_attempts to retrieve NTP server URL(s)..."
    get_ntp_hosts

    if [ "$hostName" ] || [ "$hostName2" ] || [ "$hostName3" ] || [ "$hostName4" ] || [ "$hostName5" ]; then
        break
    fi

     # If this is the last attempt, try bootstrap as fallback and then break
    if [ $attempts -eq $max_attempts ]; then
        ntpLog "TR-181 returned empty NTP server list; falling back to /opt/secure/RFC/bootstrap.ini..."
        get_ntp_hosts_from_bootstrap
        break
    fi

    sleep 3
    attempts=$((attempts + 1))

done

# Use default polling intervals if not configured
# Validate that minPoll is not greater than maxPoll
[ -z "$minPoll" ] && minPoll="$DEFAULT_MINPOLL"
[ -z "$maxPoll" ] && maxPoll="$DEFAULT_MAXPOLL"

if [ "$minPoll" -gt "$maxPoll" ]; then
    ntpLog "ERROR: minPoll ($minPoll) is greater than maxPoll ($maxPoll), resetting both to defaults ($DEFAULT_MINPOLL/$DEFAULT_MAXPOLL)"
    minPoll="$DEFAULT_MINPOLL"
    maxPoll="$DEFAULT_MAXPOLL"
fi
ntpLog "Minpoll:$minPoll MaxPoll:$maxPoll"

hosts=("$hostName" "$hostName2" "$hostName3" "$hostName4" "$hostName5")
directives=("$directive1" "$directive2" "$directive3" "$directive4" "$directive5")
ntpLog "NTP Server URL for the partner:${hosts[*]}"

conf_written=0
> "$CHRONY_CONF"

# Add makestep directive to chrony config to control threshold/step correction
if [ -n "$maxstep" ]; then
    if echo "$maxstep" | grep -Eq '^[0-9]+(\.[0-9]+)?,[0-9]+$'; then
        stepval="${maxstep%%,*}"
        stepcount="${maxstep##*,}"
        echo "makestep $stepval $stepcount" >> "$CHRONY_CONF"
        ntpLog "Added makestep $stepval $stepcount to $CHRONY_CONF"
    else
        echo "makestep 1.0 3" >> "$CHRONY_CONF"
        ntpLog "NTPMaxstep value '$maxstep' is invalid, using default makestep 1.0 3 in $CHRONY_CONF"
    fi
else
    echo "makestep 1.0 3" >> "$CHRONY_CONF"
    ntpLog "NTPMaxstep is not set, using default makestep 1.0 3 in $CHRONY_CONF"
fi

# Add NTP servers ("server" or "pool" directive) to the configuration file

for i in $(seq 0 4); do
    host="${hosts[$i]}"
    directive="${directives[$i]}"
    if [ -n "$host" ]; then
        # use directive if set, else default to server
        [ -z "$directive" ] && directive="server"

         # Only allow server or pool, default to server otherwise
        if [ "$directive" != "server" ] && [ "$directive" != "pool" ]; then
            directive="server"
        fi

        if [ "$directive" = "pool" ]; then
           printf "%s %s iburst minpoll %s maxpoll %s maxsources 4\n" "$directive" "$host" "$minPoll" "$maxPoll" >> "$CHRONY_CONF"
        else
           printf "%s %s iburst minpoll %s maxpoll %s\n" "$directive" "$host" "$minPoll" "$maxPoll" >> "$CHRONY_CONF" 
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

# Fallback: If no valid NTP hosts found, use Google's public time server
if [ "$conf_written" -eq 0 ]; then
    printf "server time.google.com iburst minpoll %s maxpoll %s\n" "$minPoll" "$maxPoll" >> "$CHRONY_CONF"
    ntpLog "No valid NTP servers found, using fallback: time.google.com"
fi

ntpLog "Successfully updated $CHRONY_CONF"

exit 0
