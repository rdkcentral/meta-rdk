FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

DESCRIPTION = "Network Zero Config"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${MANIFEST_PATH_META_RDK}/licenses/Apache-2.0;md5=3b83ef96387f14655fc854ddc3c6bd57"

inherit systemd

SRC_URI += "file://iface-setup.service \
            file://board-access.service \
            file://default-time-setter.sh "

do_install:append() {
    install -d ${D}${systemd_unitdir}/system
    install -d ${D}${base_libdir}/rdk
    install -m 0644 ${WORKDIR}/iface-setup.service ${D}${systemd_unitdir}/system
    install -m 0644 ${WORKDIR}/board-access.service ${D}${systemd_unitdir}/system

    # Override the default time setter script which is too dependent on configs and entries from sysint
    install -m 0755 ${WORKDIR}/default-time-setter.sh ${D}${base_libdir}/rdk/
}

SYSTEMD_SERVICE:${PN} = "iface-setup.service board-access.service"

FILES:${PN} = " ${base_libdir}/rdk/default-time-setter.sh "
