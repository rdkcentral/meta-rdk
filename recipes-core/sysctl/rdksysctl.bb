SUMMARY = "Configure sysctl parameters for RDK devices"
SECTION = "configuration"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${THISDIR}/files/Apache-2.0;md5=3b83ef96387f14655fc854ddc3c6bd57"

SRC_URI = "file://50-sysctl.conf"
SRC_URI += "file://98-sysctl-mw.conf"

# Defaults (keep existing behavior unless overridden per MACHINE)
RDK_VM_DIRTY_BACKGROUND_RATIO ?= "1"
RDK_VM_DIRTY_RATIO ?= "1"

S = "${WORKDIR}"

do_install() {
    install -d ${D}${sysconfdir}/sysctl.d
    install -m 0644 ${S}/50-sysctl.conf ${D}${sysconfdir}/sysctl.d
 # Install sysctl file with substituted tunables
    sed -e "s/@RDK_VM_DIRTY_BACKGROUND_RATIO@/${RDK_VM_DIRTY_BACKGROUND_RATIO}/g" \
        -e "s/@RDK_VM_DIRTY_RATIO@/${RDK_VM_DIRTY_RATIO}/g" \
        ${S}/98-sysctl-mw.conf > ${D}${sysconfdir}/sysctl.d/98-sysctl-mw.conf
}
