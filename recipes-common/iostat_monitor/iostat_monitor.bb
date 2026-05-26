SUMMARY = "Systemd service for iostat monitoring"
DESCRIPTION = "Installs and enables a systemd service to monitor CPU usage with pidstat."

LICENSE= "CLOSED"

SRC_URI += "file://iostat-monitor.service"

inherit systemd
do_install:append() {
    install -D -m 0644 ${WORKDIR}/iostat-monitor.service ${D}${systemd_system_unitdir}/iostat-monitor.service
}

SYSTEMD_SERVICE:${PN} = "iostat-monitor.service"
SYSTEMD_AUTO_ENABLE:${PN} = "enable"
