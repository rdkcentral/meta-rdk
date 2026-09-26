SUMMARY = "RDK Chrony Configuration Update Script"
DESCRIPTION = "Script to update /etc/rdk_chrony.conf with partner NTP server URL"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${MANIFEST_PATH_META_RDK}/licenses/Apache-2.0;md5=3b83ef96387f14655fc854ddc3c6bd57"

SRC_URI = "file://update-chrony-config.sh \
           file://partner_url.conf \
          "

S = "${WORKDIR}"

do_install() {
    install -d ${D}${sbindir}
    install -d ${D}${sysconfdir}
    
    install -m 0755 ${WORKDIR}/update-chrony-config.sh ${D}${sbindir}/update-chrony-config.sh
    install -m 0644 ${WORKDIR}/partner_url.conf ${D}${sysconfdir}/partner_url.conf
}

FILES:${PN} = "${sbindir}/update-chrony-config.sh \
               ${sysconfdir}/partner_url.conf \
              "
