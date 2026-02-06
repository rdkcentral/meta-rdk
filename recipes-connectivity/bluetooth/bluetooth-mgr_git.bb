SUMMARY = "bluetooth-mgr"
SECTION = "console/utils"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=175792518e4ac015ab6696d16c4f607e"

PV = "1.0.8"
PR = "r2"

SRCREV_FORMAT = "bluetooth-mgr"
SRCREV = "8b8aa5c0bf9b691b3c6cb874bdafea0c488bf46f"
PACKAGE_ARCH = "${MIDDLEWARE_ARCH}"
SRC_URI = "${CMF_GITHUB_ROOT}/bluetooth_mgr;${CMF_GITHUB_SRC_URI_SUFFIX}"
SRC_URI:append = " file://btmgr.conf"
S = "${WORKDIR}/git"

DEPENDS = "bluetooth-core cjson wpeframework-clientlibraries"

RDEPENDS:${PN}  = " bluetooth-core"
RDEPENDS:${PN} += " cjson"


DEPENDS += " ${@bb.utils.contains('DISTRO_FEATURES', 'gstreamer1', 'gstreamer1.0 gstreamer1.0-plugins-base', '', d)}"
ENABLE_GST1 = "--enable-gstreamer1=${@bb.utils.contains('DISTRO_FEATURES', 'gstreamer1', 'yes', 'no', d)}"
EXTRA_OECONF = " ${ENABLE_GST1}"

ENABLE_SAFEC = "--enable-safec=${@bb.utils.contains('DISTRO_FEATURES', 'safec','yes', 'no', d)}"
EXTRA_OECONF += " ${ENABLE_SAFEC}"

# RPC-IARM Must be Enabled for Video Platforms only; Also iarmbus is dependency for Video Platforms
DEPENDS:append:client = " iarmbus ${@bb.utils.contains('DISTRO_FEATURES', 'ENABLE_NETWORKMANAGER', '', 'netsrvmgr', d)}"
DEPENDS:append:hybrid = " iarmbus"
EXTRA_OECONF:append:client = " --enable-rpc"
EXTRA_OECONF:append:hybrid = " --enable-rpc"

DEPENDS += " fcgi"
DEPENDS += " rfc"

DEPENDS += " telemetry"
RDEPENDS:${PN} += " telemetry"

DEPENDS += " rdk-logger"
RDEPENDS:${PN} += " rdk-logger"

DEPENDS += " commonutilities"
DEPENDS += " rdkfwupgrader"
DEPENDS += " libsyswrapper"


DEPENDS:append:client = " virtual/vendor-media-utils"
DEPENDS:append:client = " audiocapturemgr"
RDEPENDS:${PN}:append:client = " virtual/vendor-media-utils"
RDEPENDS:${PN}:append:client = " audiocapturemgr"

DEPENDS:append:hybrid = " virtual/vendor-media-utils"
DEPENDS:append:hybrid = " audiocapturemgr"
RDEPENDS:${PN}:append:hybrid = " virtual/vendor-media-utils"
RDEPENDS:${PN}:append:hybrid = " audiocapturemgr"


inherit autotools pkgconfig systemd coverity syslog-ng-config-gen logrotate_config
SYSLOG-NG_FILTER = "btmgr"
SYSLOG-NG_SERVICE_btmgr = "btmgr.service"
SYSLOG-NG_DESTINATION_btmgr = "btmgrlog.txt"
SYSLOG-NG_LOGRATE_btmgr = "very-high"

LOGROTATE_NAME    = "btmgr"
LOGROTATE_LOGNAME_btmgr = "btmgrlog.txt"
LOGROTATE_SIZE_MEM_btmgr    = "250000"
LOGROTATE_ROTATION_MEM_btmgr  = "2"
LOGROTATE_SIZE_btmgr    = "512000"
LOGROTATE_ROTATION_btmgr  = "5"

# Breakpad processname and logfile mapping
inherit breakpad-wrapper breakpad-logmapper
BREAKPAD_LOGMAPPER_PROCLIST = "btMgrBus"
BREAKPAD_LOGMAPPER_LOGLIST = "btmgrlog.txt"

ENABLE_AC_RMF = "--enable-ac_rmf=${@bb.utils.contains('RDEPENDS:${PN}', 'virtual/${MLPREFIX}media-utils', 'yes', 'no', d)}"
ENABLE_ACM = "--enable-acm=${@bb.utils.contains('RDEPENDS:${PN}', '${MLPREFIX}audiocapturemgr', 'yes', 'no', d)}"
EXTRA_OECONF += " ${ENABLE_ACM} ${ENABLE_AC_RMF}"

EXTRA_OECONF += "--enable-rdktv-build=yes"

CFLAGS:append =" ${@bb.utils.contains('RDEPENDS:${PN}', '${MLPREFIX}audiocapturemgr', ' -I${STAGING_INCDIR}/audiocapturemgr', " ", d)}"
CFLAGS:append =" ${@bb.utils.contains('RDEPENDS:${PN}', 'virtual/${MLPREFIX}media-utils', ' -I${STAGING_INCDIR}/media-utils -I${STAGING_INCDIR}/media-utils/audioCapture', " ", d)}"

ENABLE_RDK_LOGGER = "--enable-rdk-logger=${@bb.utils.contains('RDEPENDS:${PN}', '${MLPREFIX}rdk-logger', 'yes', 'no', d)}"
EXTRA_OECONF += " ${ENABLE_RDK_LOGGER}"

# Autoconnect feature will always be enabled
ENABLE_AUTO_CONNECT = "--enable-autoconnectfeature=yes"
EXTRA_OECONF += " ${ENABLE_AUTO_CONNECT} "

EXTRA_OECONF:append = " ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', '--enable-systemd-notify', '', d)}"
EXTRA_OECONF:append:client = " --enable-sys-diag"


do_install:append() {
    install -d ${D}${systemd_unitdir}/system ${D}${sysconfdir}
    install -m 0644 ${S}/conf/btmgr.service ${D}${systemd_unitdir}/system
}


BTMGR_STARTUP_DELAY ?= "3"

do_install:append() {
    sed -i -- "s/##BTMGR_STARTUP_DELAY##/${BTMGR_STARTUP_DELAY}/" ${WORKDIR}/btmgr.conf
    install -d ${D}${systemd_unitdir}/system/btmgr.service.d
    install -D -m 0644 ${WORKDIR}/btmgr.conf ${D}${systemd_unitdir}/system/btmgr.service.d/btmgr.conf
}

SYSTEMD_SERVICE:${PN}  = "btmgr.service"

FILES:${PN} += "${systemd_unitdir}/system/btmgr.service"
FILES:${PN} += "${systemd_unitdir}/system/btmgr.service.d/btmgr.conf"


