SUMMARY = "Start parodus"
DESCRIPTION = "Frames the parodus command line and starts the parodus process"
SECTION = "console/utils"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

SRCREV = "59ae4d558ef95b8ebdd4fd4d97833cfb2baf9aa7"
SRC_URI = "${CMF_GITHUB_ROOT}/start-parodus;${CMF_GITHUB_SRC_URI_SUFFIX};name=start-parodus"

PV = "2.5.0"
PR = "r0"
PACKAGE_ARCH = "${MIDDLEWARE_ARCH}"
S = "${WORKDIR}/git"

DEPENDS = "cjson rfc"
RDEPENDS:${PN} = "cjson rfc parodus"

CFLAGS:append = " -I${STAGING_INCDIR}/cjson"
LDFLAGS += "-lcjson -lrfcapi"

EXTRA_OECONF += "--with-platform=DEVICE_VIDEO"

inherit autotools pkgconfig systemd syslog-ng-config-gen logrotate_config

SYSLOG-NG_FILTER = "parodus"
SYSLOG-NG_SERVICE_parodus = "parodus.service"
SYSLOG-NG_DESTINATION_parodus = "parodus.log"
SYSLOG-NG_LOGRATE_parodus = "high"

LOGROTATE_NAME = "parodus"
LOGROTATE_LOGNAME_parodus = "parodus.log"
#HDD_ENABLE
LOGROTATE_SIZE_parodus = "128000"
LOGROTATE_ROTATION_parodus = "3"
#HDD_DISABLE
LOGROTATE_SIZE_MEM_parodus = "128000"
LOGROTATE_ROTATION_MEM_parodus = "3"

do_install:append() {
    install -d ${D}${systemd_unitdir}/system
    install -m 0644 ${S}/systemd/parodus.service ${D}${systemd_unitdir}/system
    install -m 0644 ${S}/systemd/parodus_bsp.path ${D}${systemd_unitdir}/system
}

SYSTEMD_SERVICE:${PN} += "parodus.service" 
SYSTEMD_SERVICE:${PN} += "parodus_bsp.path"
FILES:${PN} += "${systemd_unitdir}/system/parodus.service" 
FILES:${PN} += "${systemd_unitdir}/system/parodus_bsp.path"
