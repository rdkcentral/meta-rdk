#
# DCM Daemon
#

DESCRIPTION = "dcmd"
SECTION = "dcmd"
DEPENDS += "rbus"
DEPENDS += "commonutilities"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=2441d6cdabdc0f370be5cd8a746eb647"

# This tells bitbake where to find the files we're providing on the local filesystem
FILESEXTRAPATHS:prepend := "${THISDIR}:"

SRCREV = "bc53578bf30fe3afa18b908dc3028f01d07c4a1d"
SRC_URI = "${CMF_GITHUB_ROOT}/dcm-agent;${CMF_GITHUB_SRC_URI_SUFFIX}"
PV = "2.0.2"
PR = "r1"
PACKAGE_ARCH = "${MIDDLEWARE_ARCH}"


# Make sure our source directory (for the build) matches the directory structure in the tarball
S = "${WORKDIR}/git"

inherit autotools coverity systemd syslog-ng-config-gen
SYSLOG-NG_FILTER = "dcmd"
SYSLOG-NG_SERVICE_dcmd = "dcmd.service"
SYSLOG-NG_DESTINATION_dcmd = "dcmscript.log"
SYSLOG-NG_LOGRATE_dcmd = "high"

# The autotools configuration I am basing this on seems to have a problem with a race condition when parallel make is enabled
PARALLEL_MAKE = ""
#RDKEMW-43
#method 1 ASNEDDED varible to empty
ASNEEDED = ""

CFLAGS:append = " -std=c11 -fPIC -D_GNU_SOURCE -Wall -Werror"

# added for certselector
EXTRA_OECONF:append = " --enable-t2api=yes --enable-iarmevent"
LDFLAGS:append = "${@bb.utils.contains('DISTRO_FEATURES', 'enable_maintenance_manager', '-lIARMBus', '', d)}"
DEPENDS += "${@bb.utils.contains('DISTRO_FEATURES', 'enable_maintenance_manager', 'iarmmgrs iarmbus', '', d)}"

CFLAGS:append += "${@bb.utils.contains('DISTRO_FEATURES', 'enable_maintenance_manager', '-DHAS_MAINTENANCE_MANAGER', '', d)}"

do_install:append () {
    install -d ${D}${bindir}
    install -d ${D}${systemd_unitdir}/system
    install -m 0644 ${S}/dcmd.service ${D}${systemd_unitdir}/system
    install -d ${D}${includedir}
    install -m 0644 ${S}/uploadstblogs/include/*.h ${D}${includedir}
}

inherit breakpad-wrapper
DEPENDS += "breakpad breakpad-wrapper"
BREAKPAD_BIN:append = "logupload"

PACKAGECONFIG:append = " breakpad"
PACKAGECONFIG[breakpad] = "--enable-breakpad,,breakpad,"

LDFLAGS += "-lbreakpadwrapper"
CXXFLAGS += "-DINCLUDE_BREAKPAD"


# Add any extra packaging if needed
FILES_${PN} += "${bindir}/logupload"
SYSTEMD_SERVICE:${PN} += "dcmd.service"
