SUMMARY = "Chrony Control Shared Library"
DESCRIPTION = "Builds and installs libchronyctl and headers"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${WORKDIR}/git/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

SRC_URI = "${CMF_GITHUB_ROOT}/time-utils.git;${CMF_GITHUB_SRC_URI_SUFFIX};name=time-utils"
SRCREV = "4cf11fb42e456193448e47005aa8f21edeb9eb45"
PV = "1.1.0"
PR = "r0"

PACKAGE_ARCH = "${MIDDLEWARE_ARCH}"

S = "${WORKDIR}/git/libchronyctl"

inherit autotools pkgconfig

DEPENDS = "chrony"

do_install:append() {
    rm -f ${D}${libdir}/libchronyctl.la
}

FILES:${PN} += "${libdir}/libchronyctl.so.*"
FILES:${PN}-dev += "${libdir}/libchronyctl.so \
                    ${includedir}/libchronyctl.h \
                    ${includedir}/addressing.h \
                    ${includedir}/candm.h"


RDEPENDS:${PN} = "chrony"
