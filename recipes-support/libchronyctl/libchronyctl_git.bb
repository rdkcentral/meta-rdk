SUMMARY = "Chrony Control Shared Library"
DESCRIPTION = "Builds and installs libchronyctl and headers"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${WORKDIR}/git/LICENSE;md5=9456f9b4a53d355fa79ca11c5c912238"

SRC_URI = "${CMF_GITHUB_ROOT}/time-utils.git;${CMF_GITHUB_SRC_URI_SUFFIX};name=time-utils"
SRCREV = "c6bf6a42b8f780bc923858af40446c56d3ae77ca"
PV = "1.1.1"
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
