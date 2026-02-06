SUMMARY = "RDK commonutilities"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=24691c8ce48996ecd1102d29eab1216e"

# To have a possibility to override SRC_URI later, we are introducing the following workaround:
SRCREV = "ae37771f8b21d67b7e9fc48c2ed9cb6bd34271b2"
SRC_URI = "${CMF_GITHUB_ROOT}/common_utilities;module=.;${CMF_GITHUB_SRC_URI_SUFFIX}"

PACKAGE_ARCH = "${MIDDLEWARE_ARCH}"
DEPENDS +=" cjson curl rdk-logger rdkcertconfig"
#RDEPENDS:{PN} += " rfc"

#uncomment the following line to turn on debugging
#CFLAGS:append = " -DCURL_DEBUG"
# or enable this distro feature
CFLAGS += "${@bb.utils.contains('DISTRO_FEATURES', 'debug_curl_cdl', ' -DCURL_DEBUG', '', d)}"

DEPENDS:append = " ${@bb.utils.contains('DISTRO_FEATURES', 'safec', ' safec', " ", d)}"
CFLAGS:append = " ${@bb.utils.contains('DISTRO_FEATURES', 'safec',  ' `pkg-config --cflags libsafec`', '-fPIC', d)}"
CFLAGS:append = " ${@bb.utils.contains('DISTRO_FEATURES', 'safec', '', ' -DSAFEC_DUMMY_API', d)}"
LDFLAGS:append = " ${@bb.utils.contains('DISTRO_FEATURES', 'safec', ' `pkg-config --libs libsafec`', '', d)}"

LDFLAGS:append = " -lsafec -lsecure_wrapper"

CFLAGS:append = " -DRDK_LOGGER"

PV = "1.5.2"
PR = "r0"

S = "${WORKDIR}/git"

inherit autotools pkgconfig coverity
