SUMMARY = "Systemd service for pidstat CPU monitoring"
DESCRIPTION = "Installs and enables a systemd service to monitor CPU usage with pidstat."

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=175792518e4ac015ab6696d16c4f607e"

SRC_URI += "file://pidstat-cpu.service"

inherit systemd
do_install:append() {
    install -D -m 0644 ${WORKDIR}/pidstat-cpu.service ${D}${systemd_system_unitdir}/pidstat-cpu.service
}

SYSTEMD_SERVICE:${PN} = "pidstat-cpu.service"
SYSTEMD_AUTO_ENABLE:${PN} = "enable"
