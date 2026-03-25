SUMMARY = "Process Moniter utility and runner service"
DESCRIPTION = "Linux process monitor - track and record the execution times of all processes"
LICENSE = "Apache-2.0"

LIC_FILES_CHKSUM = "file://LICENSE;md5=894d9b830cb1f38db58741000f9c2c7f"

S = "${WORKDIR}/git"
# TODO: B = "${WORKDIR}/git/build"
# SRC_URI = "git://github.com/TeknoVenus/ProcessMonitor.git;branch=main"
SRC_URI = "git://github.com/gomathishankar37/ProcessMonitor.git;protocol=https;branch=exit-handler"

SRC_URI:append = " file://process-monitor.path \
                   file://process-monitor.service \
                   "

inherit cmake systemd

EXTRA_OECMAKE += "-DCMAKE_BUILD_TYPE=Release"

# SRCREV = "63c19611d52cec3331bc9ea1bc82175f5d8a9c96"
SRCREV = "${AUTOREV}"
PV = "1.0.0"
PR = "r0"

PACKAGE_ARCH = "${MIDDLEWARE_ARCH}"

do_install:append () {
    install -d ${D}${systemd_unitdir}/system
    rm -rf ${S}/process-monitor.service
    install -m 0644 ${WORKDIR}/process-monitor.service ${D}${systemd_unitdir}/system
    install -m 0644 ${WORKDIR}/process-monitor.path ${D}${systemd_unitdir}/system
}

SYSTEMD_SERVICE_${PN} = "process-monitor.path"

FILES:${PN} += "${systemd_system_unitdir}/process-monitor.*"
FILES:${PN} += "${bindir}/ProcessMonitor"
FILES:${PN} += "${libdir}/libexithandler.so*"
