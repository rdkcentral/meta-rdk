SUMMARY = "This receipe installs interface headers needed for timer."
SECTION = "console/utils"
DESCRIPTION = "Timer , publishing and subscription interfaces."

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

DEPENDS = "systimemgrinetrface systimemgrfactory rdk-logger libsyswrapper wpeframework-clientlibraries  telemetry rdkchronylibctrl"

SRCREV_systemtimemgr = "f7c52d3ebe5203134d7ecc242f86fbbd96d39c05"
SRC_URI = "${CMF_GITHUB_ROOT}/systemtimemgr;${CMF_GITHUB_SRC_URI_SUFFIX};name=systemtimemgr"

SRC_URI:append = " file://systimemgr.conf "
SRC_URI:append = " file://secure.conf "

PACKAGE_ARCH = "${MIDDLEWARE_ARCH}"
SRCREV_FORMAT = "systemtimemgr"
PV = "1.4.0"
PR = "r0"

CXXFLAGS += " -I${PKG_CONFIG_SYSROOT_DIR}/${includedir}/WPEFramework/powercontroller"
LDFLAGS:append = " \
	-lWPEFrameworkPowerController \
	-lchronyctl\
      "

S = "${WORKDIR}/git"
inherit autotools pkgconfig systemd syslog-ng-config-gen
SYSLOG-NG_FILTER = "systimemgr"
SYSLOG-NG_SERVICE_systimemgr = "systimemgr.service"
SYSLOG-NG_DESTINATION_systimemgr = "systimemgr.log"
SYSLOG-NG_LOGRATE_systimemgr = "low"

RDEPENDS:${PN} += "systimemgrfactory"

EXTRA_OECONF = "--enable-t2api=yes"
do_install:append() {
   install -d ${D}${systemd_unitdir}/system
   install -d ${D}${systemd_unitdir}/system/systimemgr.service.d
   install -d ${D}${sysconfdir}
   install -m 644 ${S}/systemd_units/systimemgr.service ${D}${systemd_unitdir}/system
   install -m 644 ${WORKDIR}/secure.conf ${D}${systemd_unitdir}/system/systimemgr.service.d
   install ${WORKDIR}/systimemgr.conf ${D}${sysconfdir}/
}

FILES:${PN}:append = " ${sysconfdir}/systimemgr.conf "
FILES:${PN}:append = " ${systemd_unitdir}/system/systimemgr.service "
FILES:${PN}:append = " ${systemd_unitdir}/system/systimemgr.service.d/secure.conf "
SYSTEMD_SERVICE:${PN} = "systimemgr.service"

