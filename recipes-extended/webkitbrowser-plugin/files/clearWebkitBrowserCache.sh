#!/bin/sh
#Script to clear browser cache on bootup after CDL

CUR_IMAGE=$(grep "^imagename" /version.txt | cut -d ':' -f2)
CACHE_CLEAR_VER_FILE="/opt/.browser_cache_clear_version"
PREV_IMAGE=$(cat ${CACHE_CLEAR_VER_FILE})
BROWSER_CACHE_DIR=$(grep diskcachedir /etc/WPEFramework/plugins/ResidentApp.json | cut -d "\"" -f4)

if [ -f $CACHE_CLEAR_VER_FILE ] && [ "x$CUR_IMAGE" == "x$PREV_IMAGE" ]; then
    echo "Browser cache is not removed, previous reboot is not due to CDL"
else
    echo "Removing browser cache on bootup, Cache Dir : $BROWSER_CACHE_DIR"
    rm -rf $BROWSER_CACHE_DIR
    echo $CUR_IMAGE > $CACHE_CLEAR_VER_FILE
fi
