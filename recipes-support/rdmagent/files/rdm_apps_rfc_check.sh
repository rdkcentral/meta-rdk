#!/bin/sh
##########################################################################
# If not stated otherwise in this file or this component's LICENSE
# file the following copyright and licenses apply:
#
# Copyright 2021 RDK Management
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
if [ -f /etc/device.properties ];then
    . /etc/device.properties
fi

DOWNLOAD_APP_MODULE=$1

#Read the RFC value for the app name
if [ "$DEVICE_TYPE" = "broadband" ]; then
    RFC_VALUE=`dmcli eRT getv Device.DeviceInfo.X_RDKCENTRAL-COM_RFC.Feature.$DOWNLOAD_APP_MODULE.Enable | grep bool | awk '{print $5}'`
else
    if [ -f /usr/bin/tr181 ];then
        RFC_VALUE=`/usr/bin/tr181 -g Device.DeviceInfo.X_RDKCENTRAL-COM_RFC.Feature.$DOWNLOAD_APP_MODULE.Enable 2>&1 > /dev/null`
    fi
fi

echo "RFC VALUE:$RFC_VALUE"

if [ "x$RFC_VALUE" != "xtrue" ]; then
    exit 1
else
    exit 0
fi 

