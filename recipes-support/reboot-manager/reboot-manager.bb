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

PV = "2.1.0"
PR = "r0"
PACKAGE_ARCH = "${MIDDLEWARE_ARCH}"

SRC_URI = "${CMF_GITHUB_ROOT}/reboot-manager;${CMF_GITHUB_SRC_URI_SUFFIX};name=reboot-manager"
SRCREV_reboot-manager = "421b8768b6a204ba7223207519a9b9c3d0e844dc"

S = "${WORKDIR}/git"

inherit autotools coverity systemd syslog-ng-config-gen logrotate_config

SYSLOG-NG_FILTER = "reboot_reason"
SYSLOG-NG_SERVICE_reboot_reason = "reboot-reason-logger.service update-reboot-info.service"
SYSLOG-NG_DESTINATION_reboot_reason = "rebootreason.log"
SYSLOG-NG_LOGRATE_reboot_reason = "low"

LOGROTATE_NAME="reboot_reason rebootInfo"
LOGROTATE_LOGNAME_reboot_reason="rebootreason.log"
#HDD_ENABLE
LOGROTATE_SIZE_reboot_reason="1572864"
LOGROTATE_ROTATION_reboot_reason="3"
#HDD_DISABLE
LOGROTATE_SIZE_MEM_reboot_reason="1572864"
LOGROTATE_ROTATION_MEM_reboot_reason="3"

LOGROTATE_LOGNAME_rebootInfo = "rebootInfo.log"
#HDD_ENABLE
LOGROTATE_SIZE_rebootInfo = "64000"
LOGROTATE_ROTATION_rebootInfo = "3"
#HDD_DISABLE
LOGROTATE_SIZE_MEM_rebootInfo = "64000"
LOGROTATE_ROTATION_MEM_rebootInfo = "3"

DEPENDS += "commonutilities telemetry rbus"
RDEPENDS:${PN}:append = " bash"

CFLAGS:append = " -std=c11 -fPIC -D_GNU_SOURCE -Wall -Werror "
EXTRA_OECONF:append = " --enable-t2api=yes"

do_install:append() {
        install -d ${D}${bindir}
        install -d ${D}${systemd_unitdir}/system
        install -m 0644 ${S}/services/reboot-reason-logger.service ${D}${systemd_unitdir}/system
        install -m 0644 ${S}/services/update-reboot-info.path ${D}${systemd_unitdir}/system
        install -m 0644 ${S}/services/update-reboot-info.service ${D}${systemd_unitdir}/system

        if [ "${ENABLE_SYSLOGNG}" = "true" ]; then
           echo "SYSLOG_NG_ENABLED=true" >> ${D}${sysconfdir}/device-middleware.properties
           install -D -m 0644 ${S}/systemd_units/after_syslog-ng.conf ${D}${systemd_unitdir}/system/reboot-reason-logger.service.d/reboot-reason-logger.conf
        fi

        install -d ${D}${base_libdir}/rdk
        install -m 0755 ${S}/scripts/reboot-checker.sh ${D}${base_libdir}/rdk
        install -m 0755 ${S}/scripts/rebootNow.sh ${D}${base_libdir}/rdk
        ln -sf ${base_libdir}/rdk/rebootNow.sh ${D}/
}

# generating minidumps symbols
inherit breakpad-wrapper
BREAKPAD_BIN:append = " rebootnow"
PACKAGECONFIG:append = " breakpad"
PACKAGECONFIG[breakpad] = "--enable-breakpad,,breakpad,"

LDFLAGS += "-lbreakpadwrapper -lpthread -lstdc++"
CXXFLAGS += "-DINCLUDE_BREAKPAD"

SYSTEMD_SERVICE:${PN} += "reboot-reason-logger.service"
SYSTEMD_SERVICE:${PN} += "update-reboot-info.path"
SYSTEMD_SERVICE:${PN} += "update-reboot-info.service"

FILES:${PN} += "${base_libdir}/rdk/*"
FILES:${PN} += "/rebootNow.sh"
FILES:${PN} += "${bindir}/rebootnow"
