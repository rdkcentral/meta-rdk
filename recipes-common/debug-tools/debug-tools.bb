SUMMARY = "To install the script to download debug-tools"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${MANIFEST_PATH_META_RDK}/licenses/Apache-2.0;md5=3b83ef96387f14655fc854ddc3c6bd57"

SRC_URI = "file://debug-tools_download.sh"

do_install:append () {
        install -d ${D}/${sbindir}/
        install -m 0777 ${WORKDIR}/debug-tools_download.sh ${D}${sbindir}/
}

FILES:${PN} = "\
    ${sbindir}/debug-tools_download.sh \
"
