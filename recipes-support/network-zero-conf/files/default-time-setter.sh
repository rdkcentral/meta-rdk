# Enable fallback with public domain NTP servers for bench marking image


publicNTPPool="time.google.com time1.google.com time2.google.com time3.google.com time4.google.com"
NTP_CONF_FILE="/etc/systemd/timesyncd.conf"

echo "FallbackNTP=$publicNTPPool" >> ${NTP_CONF_FILE}
