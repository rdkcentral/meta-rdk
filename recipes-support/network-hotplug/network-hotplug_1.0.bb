DESCRIPTION = "Network Hotplug"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/../meta-rdk/licenses/Apache-2.0;md5=3b83ef96387f14655fc854ddc3c6bd57"

SRC_URI = " \
           file://network@.service \
           file://network.rules \
           file://lan-iface@.service \
          "

FILES_${PN} = "${sysconfdir}/udev/rules.d/* ${systemd_unitdir}/system/*"

RDEPENDS_${PN} = "udev"

do_install() {
    install -d ${D}${systemd_unitdir}/system

    if ${@bb.utils.contains('DISTRO_FEATURES', 'benchmark_enable', 'false', 'true', d)}; then  
        install -m 0644 ${WORKDIR}/*.service ${D}${systemd_unitdir}/system
    fi
    install -d ${D}${sysconfdir}/udev/rules.d
    install -m 0644 ${WORKDIR}/network.rules ${D}${sysconfdir}/udev/rules.d/network.rules
}

inherit systemd
