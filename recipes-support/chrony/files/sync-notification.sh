#!/bin/sh

echo "Starting SYNC notification"
/usr/sbin/adjtimex-sync-notify &
/lib/rdk/chrony-sync-notify.sh 

wait
