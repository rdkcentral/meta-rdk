SUMMARY = "Systemd service for pidstat CPU monitoring"
DESCRIPTION = "Installs and enables a systemd service to monitor CPU usage with pidstat."

LICENSE = "CLOSED"

SRC_URI += "file://pidstat-cpu.service"

inherit systemd
do_install:append() {
    install -D -m 0644 ${WORKDIR}/pidstat-cpu.service ${D}${systemd_system_unitdir}/pidstat-cpu.service
}

SYSTEMD_SERVICE:${PN} = "pidstat-cpu.service"
SYSTEMD_AUTO_ENABLE:${PN} = "enable"
