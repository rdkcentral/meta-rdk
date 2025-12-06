SUMMARY = "RDK version provides a shared library that can be used to access the RDK software version information."
SECTION = "console/utils"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=175792518e4ac015ab6696d16c4f607e"

PV ?= "1.0.0"
PR ?= "r0"

SRCREV ?= "d461bbd2fc8299f6e5056f488ff944e90142e9b6"
SRCREV_FORMAT     = "rdkversion"
SRCREV = "d461bbd2fc8299f6e5056f488ff944e90142e9b6"

SRC_URI = "${CMF_GITHUB_ROOT}/rdkversion;${CMF_GITHUB_SRC_URI_SUFFIX};name=rdkversion"
S = "${WORKDIR}/git"

PROVIDES = "rdkversion"
RPROVIDES:${PN} = "librdkversion.so"

PACKAGE_ARCH = "${MIDDLEWARE_ARCH}"

DEPENDS = "glib-2.0"

inherit autotools pkgconfig

INCLUDE_DIRS = " \
    -I${PKG_CONFIG_SYSROOT_DIR}/usr/include/glib-2.0 \
    -I${PKG_CONFIG_SYSROOT_DIR}/usr/lib/glib-2.0/include \
    "

CXXFLAGS += " -std=c++11 -fPIC -D_REENTRANT -rdynamic -Wall -Werror ${INCLUDE_DIRS}"

CFLAGS += " -std=c99 -Wall -Werror ${INCLUDE_DIRS}"
