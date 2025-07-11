SUMMARY = "This receipe compiles rmfcore code base. This has interface clasess that would be necessary for all the mediaplayers"
SECTION = "console/utils"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=175792518e4ac015ab6696d16c4f607e"
PACKAGE_ARCH = "${MIDDLEWARE_ARCH}"
PV ?= "1.0.0"
PR ?= "r1"

SRCREV = "a3bacc8b8660a03f3e4a1ebdb6cf466a8d190ecb"
SRCREV_FORMAT = "rdklogger"


SRC_URI = "${CMF_GITHUB_ROOT}/rdk_logger;${CMF_GITHUB_SRC_URI_SUFFIX}"

S = "${WORKDIR}/git"

DEPENDS = "log4c glib-2.0"
DEPENDS:append = " ${@bb.utils.contains('DISTRO_FEATURES', 'safec', ' safec', " ", d)}"
DEPENDS += "${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'systemd syslog-helper', '', d)}"

# RDK-E remove syslog_helper RDK-42429
DEPENDS:remove = "syslog-helper"

PACKAGECONFIG[systemd-syslog-helper] = "--enable-systemd-syslog-helper,,"

#Milestone Support
EXTRA_OECONF += " --enable-milestone"
PROVIDES = "getClockUptime"
CFLAGS:append = " -DLOGMILESTONE"
CXXFLAGS:append = " -DLOGMILESTONE"

inherit autotools pkgconfig coverity pkgconfig

CFLAGS:append = " ${@bb.utils.contains('DISTRO_FEATURES', 'safec',  ' `pkg-config --cflags libsafec`', '-fPIC', d)}"

CFLAGS:append = " ${@bb.utils.contains('DISTRO_FEATURES', 'safec', '', ' -DSAFEC_DUMMY_API', d)}"
LDFLAGS:append = " ${@bb.utils.contains('DISTRO_FEATURES', 'safec', ' `pkg-config --libs libsafec`', '', d)}"

do_configure:append:broadband () {
		#Use the RDKB Versions of the Files
		install -m 644 ${S}/rdkb_debug.ini ${S}/debug.ini
		install -m 644 ${S}/rdkb_log4crc ${S}/log4crc
}

do_install:append () {
    install -d ${D}${base_libdir}/rdk/
    install -m 0755 ${S}/scripts/logMilestone.sh ${D}${base_libdir}/rdk
}

FILES:${PN} += "${base_libdir}/rdk/logMilestone.sh \
                /rdkLogMileStone \
                /rdklogctrl \
                ${base_libdir} \
                ${base_libdir}/rdk"
