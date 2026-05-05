SUMMARY = "This receipe installs interface headers needed for timer."
SECTION = "console/utils"
DESCRIPTION = "Timer , publishing and subscription interfaces."

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://../LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

DEPENDS = "systimemgrinetrface iarmbus jsonrpc virtual/mfrlib rdk-logger telemetry"


SRCREV_systemtimemgrfactory = "784fd93ddd21dc1479077eba8bd0343b7cc0e730"
SRC_URI = "${CMF_GITHUB_ROOT}/systemtimemgr;${CMF_GITHUB_SRC_URI_SUFFIX};name=systemtimemgrfactory"

PACKAGE_ARCH = "${MIDDLEWARE_ARCH}"
SRCREV_FORMAT = "systemtimemgrfactory"

PV = "1.5.1"
PR = "r0"

ASNEEDED = ""

CXXFLAGS += " -I${PKG_CONFIG_SYSROOT_DIR}/${includedir}/rdk/iarmbus -I${PKG_CONFIG_SYSROOT_DIR}/${includedir}/rdk/iarmmgrs-hal -I${PKG_CONFIG_SYSROOT_DIR}/${includedir}/WPEFramework/powercontroller"

LDFLAGS:append = " \
	-lWPEFrameworkPowerController\
      "

S = "${WORKDIR}/git/systimerfactory"

inherit autotools pkgconfig 

RDEPENDS:${PN} += " jsonrpc curl jsoncpp "
DEPENDS += " iarmmgrs wpeframework wpeframework-clientlibraries"

EXTRA_OECONF:append = " --enable-wpevgdrm --enable-dtt --enable-t2api=yes"
EXTRA_OECONF += "${@bb.utils.contains('DISTRO_FEATURES', 'chrony', "--enable-chrony=yes", "", d)}" 
