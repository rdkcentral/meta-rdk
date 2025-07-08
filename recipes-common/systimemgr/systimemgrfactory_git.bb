SUMMARY = "This receipe installs interface headers needed for timer."
SECTION = "console/utils"
DESCRIPTION = "Timer , publishing and subscription interfaces."

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://../LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

DEPENDS = "systimemgrinetrface iarmbus jsonrpc virtual/mfrlib rdk-logger telemetry"


SRC_URI = "${CMF_GITHUB_ROOT}/systemtimemgr;${CMF_GITHUB_SRC_URI_SUFFIX};name=systemtimemgrfactory"

PACKAGE_ARCH = "${MIDDLEWARE_ARCH}"
SRCREV_FORMAT = "systemtimemgrfactory"

PV ?= "1.0.0"
PR ?= "r0"

ASNEEDED = ""

CXXFLAGS += " -I${PKG_CONFIG_SYSROOT_DIR}/${includedir}/rdk/iarmbus -I${PKG_CONFIG_SYSROOT_DIR}/${includedir}/rdk/iarmmgrs-hal -I${PKG_CONFIG_SYSROOT_DIR}/${includedir}/WPEFramework/powercontroller"

LDFLAGS:append = " \
	-lWPEFrameworkPowerController\
      "

S = "${WORKDIR}/git/systimerfactory"

inherit autotools pkgconfig 

RDEPENDS:${PN} += " jsonrpc curl jsoncpp "
DEPENDS += " iarmmgrs wpeframework wpeframework-clientlibraries telemetry" 

EXTRA_OECONF:append = " --enable-wpevgdrm --enable-dtt "
EXTRA_OECONF:append = "--enable-t2api=yes"
