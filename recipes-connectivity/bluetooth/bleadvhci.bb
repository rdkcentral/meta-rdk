DESCRIPTION = "Bluetooth LE Application to advertise on boot-up"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

SRCREV = "${AUTOREV}"

SRC_URI += "file://bleadvhci.service"
SRC_URI += "file://bleadvhci.path"

do_install:append() {
    install -d ${D}${systemd_unitdir}/system

    install -m 0644 ${WORKDIR}/bleadvhci.service       ${D}${systemd_unitdir}/system/
    install -m 0644 ${WORKDIR}/bleadvhci.path          ${D}${systemd_unitdir}/system/
}

SYSTEMD_SERVICE:${PN}:remove = " bleadvhci.service"
SYSTEMD_SERVICE:${PN} += " bleadvhci.path"

FILES:${PN} = "${sysconfdir}/* \
               ${systemd_unitdir}/system/* \
              "
inherit systemd
