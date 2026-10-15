SUMMARY = "This recipe builds the rdk_logger code base, providing logging interfaces required by all mediaplayers"
SECTION = "console/utils"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=175792518e4ac015ab6696d16c4f607e"

SRC_URI = "${CMF_GITHUB_ROOT}/rdk_logger;protocol=https;branch=main"
S = "${WORKDIR}/git"
SRCREV = "6dc321d7a5890ef947402532a990ebfb91839f2b"
PV = "3.1.0"
PR = "r0"
PACKAGE_ARCH = "${MIDDLEWARE_ARCH}"

DEPENDS = "log4c glib-2.0"
DEPENDS:append = " ${@bb.utils.contains('DISTRO_FEATURES', 'safec', ' safec', " ", d)}"

PROVIDES = "getClockUptime"
#Milestone Support
CFLAGS:append = " -DLOGMILESTONE"

inherit autotools pkgconfig coverity

CFLAGS:append = " ${@bb.utils.contains('DISTRO_FEATURES', 'safec',  ' `pkg-config --cflags libsafec`', '-fPIC', d)}"
CFLAGS:append = " ${@bb.utils.contains('DISTRO_FEATURES', 'safec', '', ' -DSAFEC_DUMMY_API', d)}"
LDFLAGS:append = " ${@bb.utils.contains('DISTRO_FEATURES', 'safec', ' `pkg-config --libs libsafec`', '', d)}"

do_install:append () {
    install -d ${D}${base_libdir}/rdk/
    install -m 0755 ${S}/scripts/logMilestone.sh ${D}${base_libdir}/rdk
}

FILES:${PN} += "${base_libdir}/rdk/logMilestone.sh \
                ${base_libdir} \
                ${base_libdir}/rdk"
