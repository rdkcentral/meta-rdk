SUMMARY = "A simple library for certificate selector/locator"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"

PV = "1.0.5"
PR = "r1"
PACKAGE_ARCH = "${MIDDLEWARE_ARCH}"

DEPENDS += "  libsyswrapper "

#code will be cloned from the following SRC_URI
SRCREV = "4dcfaf4ebf69c259c1a8032ed20cd9b604abb2f5"
SRC_URI = "git://github.com/Lasya-Prakarsha-D-V/rdk-cert-config.git;protocol=https;branch=topic/RDK-61158_oldL3"

S = "${WORKDIR}/git"
B = "${WORKDIR}/git"

CFLAGS:append = " -DCONFIG_ERROR_ENABLED -Wall -Werror"
CXXFLAGS:append = " -Wall -Werror"

inherit autotools pkgconfig coverity

TARGET_CC_ARCH += "${LDFLAGS}"

EXTRA_OECONF += "--enable-rdklogger"
