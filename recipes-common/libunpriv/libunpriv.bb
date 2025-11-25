SUMMARY = "libcap wrapper "
LICENSE = "Apache-2.0"
DEPENDS = "libcap jsoncpp"
S = "${WORKDIR}/git"
SRC_URI = "${CMF_GITHUB_ROOT}/rdk-libunpriv.git;${CMF_GITHUB_SRC_URI_SUFFIX};name=rdk-libunpriv"

SRCREV_rdk-libunpriv = "547d202d421ed83bd60b677b5d057cad3b7ae8ad"
SRCREV_FORMAT = "rdk-libunpriv"
CXXFLAGS:append = "\
    -I${STAGING_INCDIR} \
    -I${STAGING_INCDIR}/jsoncpp"

LIC_FILES_CHKSUM = "file://LICENSE;md5=175792518e4ac015ab6696d16c4f607e"

inherit autotools pkgconfig

CFLAGS += " -Wall -Werror -Wextra -Wno-unused-parameter "
