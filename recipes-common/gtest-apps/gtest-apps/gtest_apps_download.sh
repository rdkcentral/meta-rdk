#!/bin/sh
##########################################################################
# If not stated otherwise in this file or this component's Licenses.txt
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


DNLD_SCRIPT=/etc/rdm/downloadMgr.sh
LOG_FILE=/rdklogs/logs/gtestapp.log
RDM_DNLD_PATH=/tmp/rdm/downloads
COMPONENT_NAME=$1
APP_NAME_PREFIX=gtestapp-
APP_NAME=$APP_NAME_PREFIX$COMPONENT_NAME
GTEST_APPS_PATH="/tmp/$APP_NAME"
VALIDATION_METHOD=openssl
PKG_EXT=ipk

# log format
DT_TIME=`date +'%Y-%m-%d:%H:%M:%S:%6N'`
echo_t()
{
    echo "$DT_TIME $@"
}

if [ ! -f $LOG_FILE ]; then
    touch $LOG_FILE
fi

clean_gtest_app()
{
    echo_t "$GTESTAPP Parameter for clean_gtest_app() is $1" >> $LOG_FILE
    umount /tmp/$APP_NAME_PREFIX$1
    rm -rf /tmp/$APP_NAME_PREFIX$1
    rm -rf $RDM_DNLD_PATH/$APP_NAME_PREFIX$1
}

download_all_gtest_apps()
{
    APP_COUNT=`cat /etc/rdm/rdm-manifest.json|grep "app_name"|wc -l`
    IDX=0

    while [ "$IDX" -lt "$APP_COUNT" ]
    do
        DOWNLOAD_APP_NAME=`/usr/bin/jsonquery -f /etc/rdm/rdm-manifest.json  --path=//packages/$IDX/app_name`
        if [ $? -eq 0 ]; then
            GTESTAPP=`echo $DOWNLOAD_APP_NAME |grep -i gtestapp`
            if [ "$GTESTAPP" != "" ];then
                if [ -d "/tmp/$GTESTAPP" ]; then
                    echo "$GTESTAPP is already present. Removing the existing $GTESTAPP directories"
                    echo_t "[GTEST_APPS] $GTESTAPP is already present. Removing the existing $GTESTAPP directories" >> $LOG_FILE
                    COMPONENT=$(echo $GTESTAPP| cut -d'-' -f 2)
                    clean_gtest_app $COMPONENT
                fi
                echo "Starting download of gtest app : $GTESTAPP"
                sh $DNLD_SCRIPT $GTESTAPP "" $VALIDATION_METHOD $PKG_EXT ""
                APP_DL_STATUS=$?
                if [ $APP_DL_STATUS -eq 0 ]; then
                    echo "$GTESTAPP download is success"
                    echo_t "[GTEST_APPS] $GTESTAPP download is success" >> $LOG_FILE
                else
                    echo "$GTESTAPP download has failed"
                    echo_t "[GTEST_APPS] $GTESTAPP download has failed, result is $APP_DL_STATUS" >> $LOG_FILE
                fi
            fi
        else
            break
        fi
        IDX=`expr $IDX + 1`
    done
}

# To download all the gtest apps mentioned in rdm-manifest.json file
if [ "$#" -eq  "0" ]; then
    echo_t "[GTEST_APPS] Empty parameter.Downloading all gtest apps" >> $LOG_FILE
    download_all_gtest_apps

#Cleanup the downloaded gtest apps
elif [ $1 == "cleanall" ];then
    #Clean all downloaded gtest apps
    if [ -z $2 ];then
        echo_t "[GTEST_APPS] Clean all the gtest apps" >> $LOG_FILE
        clean_gtest_app "*"
    #Clean the specified gtest app
    else
        echo_t "[GTEST_APPS] Cleanup gtest app : $2" >> $LOG_FILE
        clean_gtest_app $2
    fi

#download the specified gtest app
else
    echo_t "[GTEST_APPS] Parameter for script is $COMPONENT_NAME" >> $LOG_FILE
    if [ -d $GTEST_APPS_PATH ]; then
        echo "$APP_NAME is already present.Removing the existing $APP_NAME directories"
        echo_t "[GTEST_APPS] $APP_NAME is already present. Removing the existing $APP_NAME directories" >> $LOG_FILE
        clean_gtest_app $COMPONENT_NAME
    fi
    echo "Starting download of $APP_NAME"
    sh $DNLD_SCRIPT $APP_NAME "" $VALIDATION_METHOD $PKG_EXT ""
    DNLD_RES=$?

    if [ $DNLD_RES -eq 0 ]; then
        echo "$APP_NAME download is success"
        echo_t "[GTEST_APPS] $APP_NAME download is success" >> $LOG_FILE
    else
        echo " $APP_NAME download has failed"
        echo_t "[GTEST_APPS] $APP_NAME download has failed, result is $DNLD_RES " >> $LOG_FILE
    fi
fi

