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
. /etc/device.properties

APP_MOUNT_PATH=/media/apps

if [ "x$HDD_ENABLED" == "xtrue" ]; then
    if [ ! -d $HDD_APP_MOUNT_PATH ]; then
       mkdir -p $HDD_APP_MOUNT_PATH
    fi

    # Mount the app download directory of HDD to /media/apps
    checkmount=`mount | grep -i $APP_MOUNT_PATH`
    if [ ! "$checkmount" ]; then
        echo "Mounting $HDD_APP_MOUNT_PATH into $APP_MOUNT_PATH"
        mount --bind $HDD_APP_MOUNT_PATH $APP_MOUNT_PATH
    fi
fi
