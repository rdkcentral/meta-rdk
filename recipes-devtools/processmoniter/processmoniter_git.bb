SUMMARY = "Process Moniter utility and runner service"
DESCRIPTION = "Linux process monitor - track and record the execution times of all processes"
LICENSE = "Apache-2.0"

LIC_FILES_CHKSUM = "file://LICENSE;md5=894d9b830cb1f38db58741000f9c2c7f"

S = "${WORKDIR}/git"
SRC_URI = "git://github.com/TeknoVenus/ProcessMonitor.git;branch=main"
SRCREV = "dc9c218e78c2e1a766df3d439b90f471da3db8fb"

inherit cmake systemd

SRCREV = "63c19611d52cec3331bc9ea1bc82175f5d8a9c96"
PV = "1.0.0"
PR = "r0"

PACKAGE_ARCH = "${MIDDLEWARE_ARCH}"

do_install_append () {
    install -d ${D}${systemd_unitdir}/system
    rm -rf ${S}/process-monitor.service
    install -m 0644 ${S}/process-monitor.service ${D}${systemd_unitdir}/system
    install -m 0644 ${S}/process-monitor.path ${D}${systemd_unitdir}/system
}

SYSTEMD_SERVICE_${PN} = "process-monitor.path"

FILES_${PN} += "${systemd_system_unitdir}/process-monitor.*"
FILES_${PN} += "${bindir}/ProcessMonitor"

