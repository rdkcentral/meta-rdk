SUMMARY = "This recipe compiles DCM json parser code base"
SECTION = "console/utils"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=175792518e4ac015ab6696d16c4f607e"

DEPENDS= "cjson telemetry safec-common-wrapper"
DEPENDS:append = " ${@bb.utils.contains('DISTRO_FEATURES', 'safec', ' safec', " ", d)}"
RDEPENDS:${PN} = "cjson"

SRC_URI = "${CMF_GIT_ROOT}/rdk/components/generic/dcm;protocol=${CMF_GIT_PROTOCOL};branch=${CMF_GIT_BRANCH}"

PV = "${RDK_RELEASE}+git${SRCPV}"
SRCREV ?= "8095cb6a22ba8087a853080f0d2acaeeb6a46878"
S = "${WORKDIR}/git"

CFLAGS:append = " \
    -I${STAGING_INCDIR} \
    -I${STAGING_INCDIR}/cjson \
"
CFLAGS:append = " -I${STAGING_INCDIR}/ccsp -I${STAGING_INCDIR}/dbus-1.0 -I${STAGING_LIBDIR}/dbus-1.0/include/ "
LDFLAGS:append = " -ltelemetry_msgsender "

inherit autotools systemd coverity pkgconfig

CFLAGS:append = " ${@bb.utils.contains('DISTRO_FEATURES', 'safec',  ' `pkg-config --cflags libsafec`', '-fPIC', d)}"

CFLAGS:append = " ${@bb.utils.contains('DISTRO_FEATURES', 'safec', '', ' -DSAFEC_DUMMY_API', d)}"
LDFLAGS:append = " ${@bb.utils.contains('DISTRO_FEATURES', 'safec', ' `pkg-config --libs libsafec`', '', d)}"
