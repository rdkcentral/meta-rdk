SUMMARY = "Configure sysctl parameters for RDK devices"
SECTION = "configuration"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${THISDIR}/files/Apache-2.0;md5=3b83ef96387f14655fc854ddc3c6bd57"

SRC_URI = "file://50-sysctl.conf"
SRC_URI += "file://98-sysctl-mw.conf"

# Defaults (keep existing behavior unless overridden per MACHINE)
RDK_VM_DIRTY_BACKGROUND_RATIO ?= "1"
RDK_VM_DIRTY_RATIO ?= "1"
RDK_VM_SWAPPINESS ?= "80"
RDK_VM_VFS_CACHE_PRESSURE ?= "200"
RDK_VM_WATERMARK_SCALE_FACTOR ?= "40" 

S = "${WORKDIR}"

do_install() {
    install -d ${D}${sysconfdir}/sysctl.d
    install -m 0644 ${S}/50-sysctl.conf ${D}${sysconfdir}/sysctl.d
 # Generate 98-sysctl-mw.conf with machine-configurable values
    sed -e "s/@RDK_VM_DIRTY_BACKGROUND_RATIO@/${RDK_VM_DIRTY_BACKGROUND_RATIO}/g" \
        -e "s/@RDK_VM_DIRTY_RATIO@/${RDK_VM_DIRTY_RATIO}/g" \
        -e "s/@RDK_VM_SWAPPINESS@/${RDK_VM_SWAPPINESS}/g" \
        -e "s/@RDK_VM_VFS_CACHE_PRESSURE@/${RDK_VM_VFS_CACHE_PRESSURE}/g" \
        -e "s/@RDK_VM_WATERMARK_SCALE_FACTOR@/${RDK_VM_WATERMARK_SCALE_FACTOR}/g" \
        ${S}/98-sysctl-mw.conf > ${D}${sysconfdir}/sysctl.d/98-sysctl-mw.conf

    chmod 0644 ${D}${sysconfdir}/sysctl.d/98-sysctl-mw.conf
}
