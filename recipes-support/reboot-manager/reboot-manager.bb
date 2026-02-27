#
# Recipe for reboot-manager: Installs a binary utility to perform system reboots
# and log the reasons for those reboots, supporting diagnostics
# and telemetry integration.
#

DESCRIPTION = "reboot-manager: Binary Utility to initiate system reboots and log detailed reboot reasons, aiding diagnostics and telemetry"
SECTION = "reboot-manager"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"

FILESEXTRAPATHS:prepend := "${THISDIR}:"

PV = "1.0.0"
PR = "r0"
PACKAGE_ARCH = "${MIDDLEWARE_ARCH}"

SRC_URI = "${CMF_GITHUB_ROOT}/reboot-manager;${CMF_GITHUB_SRC_URI_SUFFIX};name=reboot-manager"
SRCREV_reboot-manager = "02d6ced58173ae8ae5a4b30263d0bc9829048e88"

S = "${WORKDIR}/git"

inherit autotools coverity systemd syslog-ng-config-gen logrotate_config
LOGROTATE_NAME="reboot_reason"
LOGROTATE_LOGNAME_reboot_reason="rebootreason.log"
#HDD_ENABLE
LOGROTATE_SIZE_reboot_reason="1572864"
LOGROTATE_ROTATION_reboot_reason="3"
#HDD_DISABLE
LOGROTATE_SIZE_MEM_reboot_reason="1572864"
LOGROTATE_ROTATION_MEM_reboot_reason="3"

DEPENDS += "commonutilities telemetry rbus"
RDEPENDS:${PN}:append = " bash"

CFLAGS:append = " -std=c11 -fPIC -D_GNU_SOURCE -Wall -Werror "
EXTRA_OECONF:append = " --enable-t2api=yes"

# generating minidumps symbols
inherit breakpad-wrapper
BREAKPAD_BIN:append = " rebootnow"
PACKAGECONFIG:append = " breakpad"
PACKAGECONFIG[breakpad] = "--enable-breakpad,,breakpad,"

LDFLAGS += "-lbreakpadwrapper -lpthread -lstdc++"
CXXFLAGS += "-DINCLUDE_BREAKPAD"

FILES:${PN} += "${bindir}/rebootnow"
