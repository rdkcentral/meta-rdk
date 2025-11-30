SUMMARY = "Configure sysctl parameters for RDK devices"
SECTION = "configuration"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${THISDIR}/files/Apache-2.0;md5=3b83ef96387f14655fc854ddc3c6bd57"

SRC_URI = "file://50-sysctl.conf"
SRC_URI += "file://98-sysctl-mw.conf"

PACKAGE_ARCH = "${MIDDLEWARE_ARCH}"

S = "${WORKDIR}"

do_install() {
    install -d ${D}${sysconfdir}/sysctl.d
    install -m 0644 ${S}/50-sysctl.conf ${D}${sysconfdir}/sysctl.d
    install -m 0644 ${S}/98-sysctl-mw.conf ${D}${sysconfdir}/sysctl.d
}
