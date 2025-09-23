SUMMARY = "This recipe builds the rdk_logger code base, providing logging interfaces required by all mediaplayers"
SECTION = "console/utils"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=175792518e4ac015ab6696d16c4f607e"
PACKAGE_ARCH = "${MIDDLEWARE_ARCH}"
PV = "2.0.0"
PR = "r0"

SRCREV = "6d49a9a630ae6a6121b29c1fda20a65655757a80"

SRC_URI = "${CMF_GITHUB_ROOT}/rdk_logger;protocol=https;branch=topic/RDKEMW-8528"

S = "${WORKDIR}/git"

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

FILES:${PN} += "${base_libdir}/rdk/logMilestone.sh \
                /rdkLogMileStone \
                /rdklogctrl \
                ${base_libdir} \
                ${base_libdir}/rdk"
