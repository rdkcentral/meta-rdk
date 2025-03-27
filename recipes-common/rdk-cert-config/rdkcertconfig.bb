SUMMARY = "A simple library for certificate selector/locator"
LICENSE = "CLOSED"

PACKAGE_ARCH = "${MIDDLEWARE_ARCH}"
PV = "1.0"

DEPENDS += "  libsyswrapper "

SRC_URI = "${CMF_GITHUB_ROOT}/rdk-cert-config;${CMF_GITHUB_SRC_URI_SUFFIX}"

S = "${WORKDIR}/git"
B = "${WORKDIR}/git"
inherit autotools pkgconfig coverity

TARGET_CC_ARCH += "${LDFLAGS}"

EXTRA_OECONF += "--enable-rdklogger"
