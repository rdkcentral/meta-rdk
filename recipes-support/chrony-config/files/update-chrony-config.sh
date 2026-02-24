#!/bin/sh
##########################################################################
# If not stated otherwise in this file or this component's LICENSE
# file the following copyright and licenses apply:
#
# Copyright 2026 RDK Management
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

# Script to update /etc/rdk_chrony.conf with partner NTP server URL

# Allow override via environment variables for testing
PARTNER_URL_FILE="${PARTNER_URL_FILE:-/etc/partner_url.conf}"
CHRONY_CONF="${CHRONY_CONF:-/etc/rdk_chrony.conf}"

# Function to read partner URL from configuration file
read_partner_url() {
    if [ -f "$PARTNER_URL_FILE" ]; then
        # Read the URL from the file (expect format: PARTNER_NTP_URL=<url>)
        # Use cut -d'=' -f2- to handle URLs with = characters
        URL=$(grep -E "^PARTNER_NTP_URL=" "$PARTNER_URL_FILE" | cut -d'=' -f2- | tr -d ' ')
        
        if [ -n "$URL" ]; then
            echo "$URL"
            return 0
        fi
    fi
    
    # If no URL found in file, check environment variable
    if [ -n "$PARTNER_NTP_URL" ]; then
        echo "$PARTNER_NTP_URL"
        return 0
    fi
    
    return 1
}

# Function to validate URL/hostname
validate_url() {
    local url="$1"
    
    # Check for newlines, which could allow config injection
    # Use POSIX-compliant approach by checking if the string contains a newline
    case "$url" in
        *"
"*)
            # Contains a newline
            return 1
            ;;
    esac
    
    # NTP servers in chrony should be hostnames or IP addresses, not full URLs
    # Basic validation: hostname/URL should only contain alphanumeric, dots, hyphens, underscores, and colons (for ports)
    # This regex allows for hostnames, IP addresses, and URLs with ports
    if ! echo "$url" | grep -qE '^[a-zA-Z0-9]([a-zA-Z0-9._:-]*[a-zA-Z0-9])?$'; then
        return 1
    fi
    
    return 0
}

# Main execution
PARTNER_URL=$(read_partner_url)

if [ -n "$PARTNER_URL" ]; then
    # Validate the URL before using it
    if ! validate_url "$PARTNER_URL"; then
        echo "Error: Invalid partner NTP URL format: $PARTNER_URL"
        echo "URL must contain only alphanumeric characters, dots, hyphens, underscores, and colons"
        exit 1
    fi
    
    echo "Updating $CHRONY_CONF with partner NTP server: $PARTNER_URL"
    
    # Create directory if it doesn't exist
    mkdir -p "$(dirname "$CHRONY_CONF")"
    
    # Write the chrony server configuration
    # Format: server $URL iburst minpoll 10 maxpoll 12
    # Use proper quoting to prevent shell injection
    echo "server $PARTNER_URL iburst minpoll 10 maxpoll 12" > "$CHRONY_CONF"
    
    echo "Successfully updated $CHRONY_CONF"
    exit 0
else
    echo "Error: Partner NTP URL not found in $PARTNER_URL_FILE or PARTNER_NTP_URL environment variable"
    exit 1
fi
