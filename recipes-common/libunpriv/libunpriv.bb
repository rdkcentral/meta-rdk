SUMMARY = "libcap wrapper "
LICENSE = "Apache-2.0"
DEPENDS = "libcap jsoncpp"
S = "${WORKDIR}/git"
PV = "1.0.2"
PR = "r0"
PACKAGE_ARCH = "${MIDDLEWARE_ARCH}"
SRCREV_rdk-libunpriv = "810b357f41605d9c3e09d2c47f9f3365da1b3b76"
SRC_URI = "${CMF_GITHUB_ROOT}/rdk-libunpriv.git;${CMF_GITHUB_SRC_URI_SUFFIX};name=rdk-libunpriv"

SRCREV_FORMAT = "rdk-libunpriv"
CXXFLAGS:append = "\
    -I${STAGING_INCDIR} \
    -I${STAGING_INCDIR}/jsoncpp"

LIC_FILES_CHKSUM = "file://LICENSE;md5=175792518e4ac015ab6696d16c4f607e"

inherit autotools pkgconfig

CFLAGS += " -Wall -Werror -Wextra -Wno-unused-parameter "
