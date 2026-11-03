
DESCRIPTION = "Linux process monitor - track and record the execution times of all processes"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=894d9b830cb1f38db58741000f9c2c7f"

#SRC_URI = "https://github.com/TeknoVenus/ProcessMonitor.git;branch=main"
SRC_URI = "git://github.com/gomathishankar37/ProcessMonitor.git;protocol=https;branch=exit-handler"
# Check-Point 1
#SRCREV = "b8836281bd7ddefd032ec7c932c656f82b6751b2" 
SRCREV = "${AUTOREV}"

S = "${WORKDIR}/git"
B = "${WORKDIR}/git/build"

PACKAGE_ARCH = "${MACHINE_ARCH}"

EXTRA_OECMAKE += "-DCMAKE_BUILD_TYPE=Release"

inherit cmake systemd

do_install:append() {
    install -d ${D}${bindir}
    install -d ${D}${systemd_unitdir}/system
    install -m 0755 ${B}/ProcessMonitor ${D}${bindir}/ProcessMonitor
    install -m 0644 ${S}/process-monitor.service ${D}${systemd_unitdir}/system
}

# Commented to not start it by default
#SYSTEMD_SERVICE:${PN} = "process-monitor.service"

FILES:${PN} += "${systemd_system_unitdir}/process-monitor.service"
FILES:${PN} += "${bindir}/ProcessMonitor"
FILES:${PN} += "${libdir}/libexithandler.so*" 
