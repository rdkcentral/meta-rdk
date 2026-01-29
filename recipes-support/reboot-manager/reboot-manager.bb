#
# RebootInfo Update
#

DESCRIPTION = "reboot-manager: Updating RebootInfo Reason"
SECTION = "reboot-manager"
LICENSE = "CLOSED"

# This tells bitbake where to find the files we're providing on the local filesystem
FILESEXTRAPATHS:prepend := "${THISDIR}:"

PV = "1.0.0"
PR = "r0"
PACKAGE_ARCH = "${MIDDLEWARE_ARCH}"

SRC_URI = "${CMF_GITHUB_ROOT}/reboot-manager;${CMF_GITHUB_SRC_URI_SUFFIX};name=reboot-manager"
SRC_URI:append = " ${RDKE_GITHUB_ROOT}/reboot-manager;${RDKE_GITHUB_SRC_URI_SUFFIX};module=.;name=rmcpc;destsuffix=git/src/rebootmanager-cpc"
SRCREV_FORMAT = "rebootmanager"
SRCREV_reboot-manager = "ae0bde055b6ebbc9aac9d98e8adc557c7c47c6fd"
SRCREV_rmcpc = "6869a96fa9303d0087a52ab8153eeab35e44b63e"

# Make sure our source directory (for the build) matches the directory structure in the tarball
S = "${WORKDIR}/git"

inherit autotools coverity systemd syslog-ng-config-gen logrotate_config
SYSLOG-NG_FILTER = "reboot-reason"
SYSLOG-NG_DESTINATION_reboot_reason = "rebootreason.log"
SYSLOG-NG_LOGRATE_reboot_reason = "low"

LOGROTATE_NAME="reboot_reason"
LOGROTATE_LOGNAME_reboot_reason="rebootreason.log"
#HDD_ENABLE
LOGROTATE_SIZE_reboot_reason="10240"
LOGROTATE_ROTATION_reboot_reason="3"
#HDD_DISABLE
LOGROTATE_SIZE_MEM_reboot_reason="10240"
LOGROTATE_ROTATION_MEM_reboot_reason="3"

DEPENDS += "commonutilities telemetry rbus"
RDEPENDS:${PN}:append = " bash"

CFLAGS:append = " -std=c11 -fPIC -D_GNU_SOURCE -Wall -Werror "
EXTRA_OECONF:append = " --enable-t2api=yes --enable-cpc=yes"

# generating minidumps symbols
inherit breakpad-wrapper
DEPENDS += "breakpad breakpad-wrapper"
BREAKPAD_BIN:append = "reboot-manager"
PACKAGECONFIG:append = " breakpad"
PACKAGECONFIG[breakpad] = "--enable-breakpad,,breakpad,"

LDFLAGS += "-lbreakpadwrapper -lpthread -lstdc++"
CXXFLAGS += "-DINCLUDE_BREAKPAD"

do_install:append() {
    install -d ${D}${bindir}

    install -m 0755 ${B}/src/rebootnow ${D}${bindir}/
    install -m 0755 ${B}/src/update-prev-reboot-info ${D}${bindir}/
}

FILES:${PN} += "${bindir}/rebootnow"
FILES:${PN} += "${bindir}/update-prev-reboot-info"
