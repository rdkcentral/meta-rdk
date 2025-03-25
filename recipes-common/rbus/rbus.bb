SUMMARY = "rbus library component"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=ed63516ecab9f06e324238dd2b259549"

SRC_URI = "git://github.com/rdkcentral/rbus.git;branch=release"
SRC_URI:append = " file://gtest_libraries_check.patch"

SRCREV = "200e91229ceb476cdb0d3252b12d719eea34c0a3"
SRCREV_FORMAT = "base"

PV ?= "2.2.0"
PR ?= "r0"

PACKAGE_ARCH = "${MIDDLEWARE_ARCH}"


S = "${WORKDIR}/git"

inherit cmake systemd pkgconfig coverity syslog-ng-config-gen breakpad-wrapper breakpad-logmapper logrotate_config
DEPENDS = "cjson msgpack-c rdk-logger linenoise breakpad breakpad-wrapper "
EXTRA_OECMAKE += " -DINCLUDE_BREAKPAD=ON "
BREAKPAD_BIN:append = " rtrouted"

# Breakpad processname and logfile mapping
BREAKPAD_LOGMAPPER_PROCLIST = "rtrouted"
BREAKPAD_LOGMAPPER_LOGLIST = "rtrouted.log"

#RDK Specific Enablements
EXTRA_OECMAKE += " -DCMAKE_BUILD_TYPE=Release "
EXTRA_OECMAKE += " -DMSG_ROUNDTRIP_TIME=ON -DENABLE_RDKLOGGER=ON"

#Gtest Specific Enablements
EXTRA_OECMAKE += " ${@bb.utils.contains('DISTRO_FEATURES', 'gtestapp', '-DENABLE_UNIT_TESTING=ON', '', d)}"
EXTRA_OECMAKE += " ${@bb.utils.contains('DISTRO_FEATURES', 'gtestapp', '-DBUILD_RBUS_BENCHMARK_TEST=ON -DBUILD_RBUS_UNIT_TEST=ON -DBUILD_RBUS_SAMPLE_APPS=ON', '', d)}"
DEPENDS:append = " ${@bb.utils.contains('DISTRO_FEATURES', 'gtestapp', ' gtest benchmark ', ' ', d)}"

#Dunfell Specific CFlags
CFLAGS:append = " -Wno-format-truncation "
CXXFLAGS:append = " -Wno-format-truncation "

SYSLOG-NG_FILTER = "rbus"
SYSLOG-NG_SERVICE_rbus = "rbus.service"
SYSLOG-NG_DESTINATION_rbus = "rtrouted.log"
SYSLOG-NG_LOGRATE_rbus = "medium"

LOGROTATE_NAME="rbus"
LOGROTATE_LOGNAME_rbus="rtrouted.log"
LOGROTATE_SIZE_rbus="1572864"
LOGROTATE_ROTATION_rbus="3"
LOGROTATE_SIZE_MEM_rbus="1572864"
LOGROTATE_ROTATION_MEM_rbus="3"

do_install:append() {
   install -d ${D}${systemd_unitdir}/system
   install -m 0644 ${S}/conf/rbus.service ${D}${systemd_unitdir}/system
   install -m 0644 ${S}/conf/rbus_session_mgr.service ${D}${systemd_unitdir}/system
   install -D -m 0644 ${S}/conf/rbus_rdkv.conf ${D}${systemd_unitdir}/system/rbus.service.d/rbus_rdkv.conf
}

PACKAGES =+ "${@bb.utils.contains('DISTRO_FEATURES', 'gtestapp', '${PN}-gtest', '', d)}"

FILES:${PN}-gtest += "\
    ${@bb.utils.contains('DISTRO_FEATURES', 'gtestapp', '${bindir}/rbus_gtest.bin', '', d)} \
"

FILES:${PN} += "${@bb.utils.contains('DISTRO_FEATURES', 'gtestapp', '${bindir}/rbus_src_gcno.tar', '', d)}"


FILES:${PN} += "${systemd_unitdir}/system/*"
SYSTEMD_SERVICE:${PN} = "rbus.service"
SYSTEMD_SERVICE:${PN}:append = " rbus_session_mgr.service "

DOWNLOAD_APPS="${@bb.utils.contains('DISTRO_FEATURES', 'gtestapp', 'gtestapp-rbus', '', d)}"
inherit comcast-package-deploy
CUSTOM_PKG_EXTNS="gtest"
SKIP_MAIN_PKG="yes"
DOWNLOAD_ON_DEMAND="yes"

