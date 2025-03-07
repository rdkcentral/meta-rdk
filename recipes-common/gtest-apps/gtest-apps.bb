
SUMMARY = "To install the script to download gtest apps"
LICENSE = "Apache-2.0"

LIC_FILES_CHKSUM = "file://${WORKDIR}/gtest_apps_download.sh;md5=231785d27cc92b83f6be308c3036fe30"

FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"
SRC_URI = "file://gtest_apps_download.sh"

do_install:append () {
    install -d ${D}/${sbindir}/
    install -m 0777 ${WORKDIR}/gtest_apps_download.sh ${D}${sbindir}/
}
