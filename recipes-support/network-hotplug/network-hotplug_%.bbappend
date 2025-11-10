FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

SRC_URI += "file://usb-ethernet.rules"

do_install_append() {
    install -d ${D}${sysconfdir}/udev/rules.d
    install -m 0644 ${WORKDIR}/usb-ethernet.rules ${D}${sysconfdir}/udev/rules.d/usb-ethernet.rules
}
