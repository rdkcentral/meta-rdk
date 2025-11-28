LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=175792518e4ac015ab6696d16c4f607e"
FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI = "git://github.com/rdkcentral/remote_debugger.git;protocol=git;nobranch=1"
SRC_URI:append:client = " file://remote_debugger_rdkv.json"
SRC_URI:append:broadband = " file://remote_debugger_rdkb.json"

# Release version - 1.2.8
# 30 June 2025
SRCREV = "6a54657f6a25c4c8e05c1bc807602a95671897d5"

PV = "1.2.8"
S = "${WORKDIR}/git"

inherit autotools pkgconfig coverity systemd syslog-ng-config-gen breakpad-logmapper

DEPENDS = "cjson rdk-logger trower-base64 msgpack-c webconfig-framework rbus libsyswrapper"
DEPENDS:append:client = " iarmbus iarmmgrs"
RDEPENDS:${PN}:append = " bash"
RDEPENDS:${PN}:remove_morty = "bash"

JSON_FILES:client = "${WORKDIR}/remote_debugger_rdkv.json"
JSON_FILES:broadband = "${WORKDIR}/remote_debugger_rdkb.json"
OUTPUT_JSON = "${S}/remote_debugger_merged.json"

LDFLAGS:client = " -lrfcapi"

INCLUDE_DIRS = " \
    -I${STAGING_INCDIR} \
    -I${STAGING_INCDIR}/trower-base64 \
    -I${STAGING_INCDIR}/rbus \
    "

DEPENDS:append:broadband = " ccsp-common-library utopia"
LDFLAGS:broadband = " -lccsp_common -lsyscfg"


INCLUDE_DIRS_broadband = " \
     -I${STAGING_INCDIR}/ccsp \
     -I${STAGING_INCDIR}/syscfg \
     "

# RBUS is now used for generic communication. Enable IARMBUS support for Video devices.
EXTRA_OECONF:append:client = " --enable-iarmbusSupport=yes"

SYSLOG-NG_FILTER = "remote-debugger"
SYSLOG-NG_SERVICE_remote-debugger = "remote-debugger.service"
SYSLOG-NG_DESTINATION_remote-debugger = "remote-debugger.log"
SYSLOG-NG_LOGRATE_remote-debugger = "high"

LOGROTATE_NAME="remotedebugger"
LOGROTATE_LOGNAME_remotedebugger="remote-debugger.log"
#HDD_ENABLE
LOGROTATE_SIZE_remotedebugger="1572864"
LOGROTATE_ROTATION_remotedebugger="3"
#HDD_DISABLE
LOGROTATE_SIZE_MEM_remotedebugger="1572864"
LOGROTATE_ROTATION_MEM_remotedebugger="3"

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

do_install:append:broadband () {
    rm -rf ${D}${sysconfdir}/rrd/remote_debugger.json
    install -m 0755 ${S}/scripts/uploadRDKBRRDLogs.sh ${D}${base_libdir}/rdk/uploadRRDLogs.sh
    install -m 0644 ${S}/scripts/remote-debugger.path ${D}${systemd_unitdir}/system
    install -m 0600 ${S}/remote_debugger_merged.json ${D}${sysconfdir}/rrd/remote_debugger.json
    
    sed -i -- 's/After=tr69hostif.service.*/After=rbus.service CcspPandMSsp.service utopia.service/g' ${D}${systemd_unitdir}/system/remote-debugger.service
    sed -i -- '/Requires=tr69hostif.service/d' ${D}${systemd_unitdir}/system/remote-debugger.service
    sed -i -- '/ExecStop=/,$d' ${D}${systemd_unitdir}/system/remote-debugger.service
}

SYSTEMD_SERVICE:${PN} += "remote-debugger.service"
SYSTEMD_SERVICE:${PN}:broadband += "remote-debugger.path"
FILES:${PN} += " \
                ${sysconfdir}/rrd/remote_debugger.json \
                ${bindir}/remotedebugger \
                ${systemd_unitdir}/system/remote-debugger.service \
                ${base_libdir}/rdk/uploadRRDLogs.sh \
"
FILES:${PN}:append:broadband += "${systemd_unitdir}/system/remote-debugger.path"
