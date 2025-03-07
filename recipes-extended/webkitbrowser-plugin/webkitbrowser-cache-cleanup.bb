SUMMARY = "WebKit cache cleanup service after SW upate"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

PV = "0.1.0"
PR = "r0"
PACKAGE_ARCH = "${MIDDLEWARE_ARCH}"

SRC_URI = "file://clearWebkitBrowserCache.sh \
           file://webkit-browser-cache-cleanup.service"

inherit systemd

do_install() {
    install -d ${D}${base_libdir}/rdk
    install -d ${D}${systemd_unitdir}/system
    install -m 0644 ${WORKDIR}/webkit-browser-cache-cleanup.service ${D}${systemd_unitdir}/system
    install -m 0755 ${WORKDIR}/clearWebkitBrowserCache.sh ${D}${base_libdir}/rdk/clearWebkitBrowserCache.sh
}

SYSTEMD_SERVICE:${PN} = "webkit-browser-cache-cleanup.service"
FILES:${PN} = "${base_libdir}/rdk/clearWebkitBrowserCache.sh ${systemd_unitdir}/system/webkit-browser-cache-cleanup.service"
