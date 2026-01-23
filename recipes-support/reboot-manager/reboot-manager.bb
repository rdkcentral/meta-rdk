#
# RebootInfo Update
#

DESCRIPTION = "reboot-manager: Updating RebootInfo Reason"
SECTION = "reboot-manager"
LICENSE = "CLOSED"

# This tells bitbake where to find the files we're providing on the local filesystem
FILESEXTRAPATHS:prepend := "${THISDIR}:"

PV:pn-reboot-manager = "1.0.0"
PR:pn-reboot-manager = "r1"
PACKAGE_ARCH:pn-reboot-manager = "${MIDDLEWARE_ARCH}"

SRC_URI = "${CMF_GITHUB_ROOT}/reboot-manager;${CMF_GITHUB_SRC_URI_SUFFIX};name=reboot-manager"
SRCREV_FORMAT = "rebootmanager"
SRCREV = "448b42ae30c34323d9f1dcfb6e2806a47b8566fe"

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

CFLAGS:append = " -std=c11 -fPIC -D_GNU_SOURCE -Wall -Werror "
EXTRA_OECONF:append = " --enable-t2api=yes"

# generating minidumps symbols
inherit breakpad-wrapper
DEPENDS += "breakpad breakpad-wrapper"
BREAKPAD_BIN:append = "reboot-manager"
PACKAGECONFIG:append = " breakpad"
PACKAGECONFIG[breakpad] = "--enable-breakpad,,breakpad,"

LDFLAGS += "-lbreakpadwrapper -lpthread -lstdc++"
CXXFLAGS += "-DINCLUDE_BREAKPAD"

FILES:${PN} += "${bindir}/rebootnow"
