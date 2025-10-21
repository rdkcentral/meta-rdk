SUMMARY = "This recipe builds the rdk_logger code base, providing logging interfaces required by all mediaplayers"
SECTION = "console/utils"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=175792518e4ac015ab6696d16c4f607e"

SRC_URI = "${CMF_GITHUB_ROOT}/rdk_logger;protocol=https;branch=main"
S = "${WORKDIR}/git"
SRCREV = "v2.3.0"
PV = "2.3.0"
PR = "r2"
PACKAGE_ARCH = "${MIDDLEWARE_ARCH}"


DEPENDS = "log4c glib-2.0"
DEPENDS:append = " ${@bb.utils.contains('DISTRO_FEATURES', 'safec', ' safec', " ", d)}"

#Milestone Support
EXTRA_OECONF += " --enable-milestone"
PROVIDES = "getClockUptime"
CFLAGS:append = " -DLOGMILESTONE"
CXXFLAGS:append = " -DLOGMILESTONE"

inherit autotools pkgconfig coverity pkgconfig

CFLAGS:append = " ${@bb.utils.contains('DISTRO_FEATURES', 'safec',  ' `pkg-config --cflags libsafec`', '-fPIC', d)}"

CFLAGS:append = " ${@bb.utils.contains('DISTRO_FEATURES', 'safec', '', ' -DSAFEC_DUMMY_API', d)}"
LDFLAGS:append = " ${@bb.utils.contains('DISTRO_FEATURES', 'safec', ' `pkg-config --libs libsafec`', '', d)}"

do_install:append () {
    install -d ${D}${base_libdir}/rdk/
    install -m 0755 ${S}/scripts/logMilestone.sh ${D}${base_libdir}/rdk
}

FILES:${PN} += "${base_libdir}/rdk/logMilestone.sh \
                /rdkLogMileStone \
                /rdklogctrl \
                ${base_libdir} \
                ${base_libdir}/rdk"
