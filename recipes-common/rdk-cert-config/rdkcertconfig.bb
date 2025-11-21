SUMMARY = "A simple library for certificate selector/locator"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"

PACKAGE_ARCH = "${MIDDLEWARE_ARCH}"
PV = "1.0"

DEPENDS += "  libsyswrapper "

#code will be cloned from the following SRC_URI
SRC_URI = "${CMF_GITHUB_ROOT}/rdk-cert-config;${CMF_GITHUB_SRC_URI_SUFFIX}"

S = "${WORKDIR}/git"
B = "${WORKDIR}/git"

CFLAGS:append = " -DCONFIG_ERROR_ENABLED -Wall -Werror"
CXXFLAGS:append = " -Wall -Werror"

inherit autotools pkgconfig coverity

TARGET_CC_ARCH += "${LDFLAGS}"

EXTRA_OECONF += "--enable-rdklogger"

SRCREV = "e0e35743f7e96ad0595ac25b11d829b766f7062b"

