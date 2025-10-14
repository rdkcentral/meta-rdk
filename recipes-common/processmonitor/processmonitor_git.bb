DESCRIPTION = "Linux process monitor - track and record the execution times of all processes"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=894d9b830cb1f38db58741000f9c2c7f"

#SRC_URI = "https://github.com/TeknoVenus/ProcessMonitor.git;branch=main"
#SRCREV = "dc9c218e78c2e1a766df3d439b90f471da3db8fb"

SRC_URI = "git://github.com/gomathishankar37/ProcessMonitor.git;protocol=https;branch=custom-config"
SRCREV = "77355998479781a68cf7e0841223a528d98bf004"

S = "${WORKDIR}/git"
B = "${WORKDIR}/git/build"

PACKAGE_ARCH = "${MACHINE_ARCH}"

EXTRA_OECMAKE += "-DCMAKE_BUILD_TYPE=Release"

inherit cmake systemd

do_install() {
    install -d ${D}${systemd_unitdir}/system
    install -m 0755 ${B}/ProcessMonitor ${D}${bindir}/ProcessMonitor
    install -m 0644 ${S}/process-monitor.service ${D}${systemd_unitdir}/system
}

# Commented to not start it by default
#SYSTEMD_SERVICE:${PN} = "process-monitor.service"

FILES:${PN} += "${systemd_system_unitdir}/process-monitor.service"
FILES:${PN} += "${bindir}/ProcessMonitor"
