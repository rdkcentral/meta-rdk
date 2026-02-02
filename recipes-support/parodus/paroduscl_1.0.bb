SUMMARY = "parodus client library"
DESCRIPTION = "C client library for parodus"
HOMEPAGE = "https://github.com/Comcast/paroduscl"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"

DEPENDS = "wrp-c nanomsg"

SRCREV = "c6b1ee637eab22ac281779546fab7b7ff171cba2"
SRC_URI = "git://github.com/Comcast/paroduscl.git"

PV = "git+${SRCPV}"
S = "${WORKDIR}/git"

inherit autotools pkgconfig
