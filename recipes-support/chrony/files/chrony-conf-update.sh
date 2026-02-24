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
# Purpose : To update the timesyncd configuration file
# Scope : RDK Devices
# Usage : Invoke by systemd service


output=""
count=0
LOG_FILE="/opt/logs/chrony.log"
attempts=1
max_attempts=5
CHRONY_CONF=/etc/rdk_chrony.conf

if [ -f /etc/env_setup.sh ]; then
    . /etc/env_setup.sh
fi

create directory if not already available
/bin/mkdir -p /var/lib/chrony

#Log framework to print timestamp and source script name
ntpLog()
{
    echo "`/bin/timestamp` : $0: $*" >> $LOG_FILE
}

# NTP URL from the property file
get_ntp_hosts() {
if [ -f /lib/rdk/getPartnerProperty.sh ]; then
     hostName=`/lib/rdk/getPartnerProperty.sh ntpHost`
     hostName2=`/lib/rdk/getPartnerProperty.sh ntpHost2`
     hostName3=`/lib/rdk/getPartnerProperty.sh ntpHost3`
     hostName4=`/lib/rdk/getPartnerProperty.sh ntpHost4`
     hostName5=`/lib/rdk/getPartnerProperty.sh ntpHost5`
fi
}

get_ntp_hosts_from_bootstrap() {
    BOOTSTRAP="/opt/secure/RFC/bootstrap.ini"

    if [ ! -f "$BOOTSTRAP" ]; then
        ntpLog "bootstrap.ini not found at $BOOTSTRAP"
        return 1
    fi

    # Helper to fetch key=value from bootstrap.ini (first match)
    get_bs_val() {
        key="$1"
        # Extract RHS after '=' and trim whitespace
        grep -m1 -E "^[[:space:]]*$key=" "$BOOTSTRAP" 2>/dev/null | \
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

partnerHostnames="$hostName $hostName2 $hostName3 $hostName4 $hostName5"
ntpLog "NTP Server URL for the partner:$partnerHostnames"

for host in $partnerHostnames; do
    # Only write non-empty hostnames
    if [ -n "$host" ]; then
        printf "server %s iburst minpoll 10 maxpoll 12\n" "$host" >> "$CHRONY_CONF"
    fi
done
printf "maxsources 1\n"  >> "$CHRONY_CONF"
ntplog "Successfully updated $CHRONY_CONF
exit 0
