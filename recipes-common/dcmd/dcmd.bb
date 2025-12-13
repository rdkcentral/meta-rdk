#
# DCM Daemon
#

DESCRIPTION = "dcmd"
SECTION = "dcmd"
DEPENDS += "rbus"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=2441d6cdabdc0f370be5cd8a746eb647"

# This tells bitbake where to find the files we're providing on the local filesystem
FILESEXTRAPATHS:prepend := "${THISDIR}:"

SRC_URI = "${CMF_GITHUB_ROOT}/dcm-agent;${CMF_GITHUB_SRC_URI_SUFFIX}"
PACKAGE_ARCH = "${MIDDLEWARE_ARCH}"

SRCREV = "03974134e21b316d4053de6574df14b3a423c8d6"

PV ?= "1.0.0"
PR ?= "r0"


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

LDFLAGS:append = "${@bb.utils.contains('DISTRO_FEATURES', 'enable_maintenance_manager', '-lIARMBus', '', d)}"

DEPENDS += "${@bb.utils.contains('DISTRO_FEATURES', 'enable_maintenance_manager', 'iarmmgrs iarmbus', '', d)}"

CFLAGS:append += "${@bb.utils.contains('DISTRO_FEATURES', 'enable_maintenance_manager', '-DHAS_MAINTENANCE_MANAGER', '', d)}"

do_install:append () {

    install -d ${D}${systemd_unitdir}/system
    install -m 0644 ${S}/dcmd.service ${D}${systemd_unitdir}/system
}

SYSTEMD_SERVICE:${PN} += "dcmd.service"

