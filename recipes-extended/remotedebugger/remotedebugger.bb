LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=175792518e4ac015ab6696d16c4f607e"
FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

PV = "1.2.9"
PR = "r0"
PACKAGE_ARCH = "${MIDDLEWARE_ARCH}"
SRCREV = "e5a8f83e250ad844c88de165c31ae83b19ee396c"
SRC_URI = "${CMF_GITHUB_ROOT}/remote_debugger;${CMF_GITHUB_SRC_URI_SUFFIX};name=generic"

SRCREV_FORMAT = "generic"
# Release version - 1.3.1
S = "${WORKDIR}/git"

CFLAGS += " -Wall -Werror"

inherit autotools pkgconfig coverity systemd syslog-ng-config-gen breakpad-logmapper

DEPENDS = "cjson rdk-logger trower-base64 msgpack-c webconfig-framework rbus libsyswrapper"
DEPENDS:append:client = " iarmbus iarmmgrs"
RDEPENDS:${PN}:append = " bash"
RDEPENDS:${PN}:remove_morty = "bash"

# Add wpeframework-clientlibraries dependency for Ent-os powermanager
DEPENDS +=  "${@bb.utils.contains('DISTRO_FEATURES', 'pwrmgr-plugin', 'wpeframework-clientlibraries', '', d)}"
RDEPENDS:${PN}:append = "${@bb.utils.contains('DISTRO_FEATURES', 'pwrmgr-plugin', ' wpeframework-clientlibraries ', '', d)}"
CFLAGS += " ${@bb.utils.contains('DISTRO_FEATURES', 'pwrmgr-plugin', " -I${PKG_CONFIG_SYSROOT_DIR}${includedir}/WPEFramework/powercontroller ", "", d)} "
CFLAGS += " ${@bb.utils.contains('DISTRO_FEATURES', 'pwrmgr-plugin', " -DPWRMGR_PLUGIN ", "", d)} "
LDFLAGS += "${@bb.utils.contains('DISTRO_FEATURES', 'pwrmgr-plugin', " -lWPEFrameworkPowerController", "", d)}"


INCLUDE_DIRS = " \
    -I${STAGING_INCDIR} \
    -I${STAGING_INCDIR}/trower-base64 \
    -I${STAGING_INCDIR}/rbus \
    "


# RBUS is now used for generic communication. Enable IARMBUS support for Video devices.
EXTRA_OECONF:append:client = " --enable-iarmbusSupport=yes"

SYSLOG-NG_FILTER = "remote-debugger"
SYSLOG-NG_SERVICE_remote-debugger = "remote-debugger.service"
SYSLOG-NG_DESTINATION_remote-debugger = "remote-debugger.log"
SYSLOG-NG_LOGRATE_remote-debugger = "high"

# Breakpad processname and logfile mapping
BREAKPAD_LOGMAPPER_PROCLIST = "remotedebugger"
BREAKPAD_LOGMAPPER_LOGLIST = "remote-debugger.log"

do_install:append () {
        install -d ${D}${bindir}/
        install -d ${D}${sysconfdir}/
        install -d ${D}${base_libdir}/rdk
        install -d ${D}${sysconfdir}/rrd/
        install -d ${D}${systemd_unitdir}/system
        install -m 0644 ${S}/scripts/remote-debugger.service ${D}${systemd_unitdir}/system
        install -m 0755 ${S}/scripts/uploadRRDLogs.sh ${D}${base_libdir}/rdk/
        install -m 0600 ${S}/remote_debugger.json ${D}${sysconfdir}/rrd/
}

SYSTEMD_SERVICE:${PN} += "remote-debugger.service"

FILES:${PN} += " \
                ${sysconfdir}/rrd/remote_debugger.json \
                ${bindir}/remotedebugger \
                ${systemd_unitdir}/system/remote-debugger.service \
                ${base_libdir}/rdk/uploadRRDLogs.sh \
"
