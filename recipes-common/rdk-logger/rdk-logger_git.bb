SUMMARY = "This recipe builds the rdk_logger code base, providing logging interfaces required by all mediaplayers"
SECTION = "console/utils"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=175792518e4ac015ab6696d16c4f607e"
PACKAGE_ARCH = "${MIDDLEWARE_ARCH}"
PV = "2.0.0"
PR = "r0"

SRCREV = "0087a47dbc6c28905f7fd4424a43ed1fc8874389"

SRC_URI = "${CMF_GITHUB_ROOT}/rdk_logger;branch=main"

S = "${WORKDIR}/git"

DEPENDS = "log4c glib-2.0"

PACKAGECONFIG[systemd-syslog-helper] = "--enable-systemd-syslog-helper,,"

#Milestone Support
EXTRA_OECONF += " --enable-milestone"
PROVIDES = "getClockUptime"
CFLAGS:append = " -DLOGMILESTONE"
CXXFLAGS:append = " -DLOGMILESTONE"

inherit autotools pkgconfig coverity pkgconfig

do_install:append () {
    install -d ${D}${base_libdir}/rdk/
    install -m 0755 ${S}/scripts/logMilestone.sh ${D}${base_libdir}/rdk
}

FILES:${PN} += "${base_libdir}/rdk/logMilestone.sh \
                /rdkLogMileStone \
                /rdklogctrl \
                ${base_libdir} \
                ${base_libdir}/rdk"
