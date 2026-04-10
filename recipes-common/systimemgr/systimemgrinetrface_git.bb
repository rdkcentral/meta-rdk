SUMMARY = "This receipe installs interface headers needed for timer."
SECTION = "console/utils"
DESCRIPTION = "Timer , publishing and subscription interfaces."

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://../LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"


SRCREV_systemtimemgrifc = "c14076fa7b1de14a8687c2a6c059cd143d143a85"
SRC_URI = "${CMF_GITHUB_ROOT}/systemtimemgr;${CMF_GITHUB_SRC_URI_SUFFIX};name=systemtimemgrifc"


S = "${WORKDIR}/git/interface"

PACKAGE_ARCH = "${MIDDLEWARE_ARCH}"

SRCREV_FORMAT = "systemtimemgrifc"

PV = "1.5.1"
PR = "r0"

inherit autotools pkgconfig 
ALLOW_EMPTY:${PN} = "1"
