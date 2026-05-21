SUMMARY = "Chrony Control Shared Library"
DESCRIPTION = "Builds and installs libchronyctl and headers"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${WORKDIR}/git/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

SRC_URI = "${CMF_GITHUB_ROOT}/time-utils.git;branch=topic/chronyctl;protocol=https"
SRCREV = "b106d7ee1bfce1500eeecd11772ffd55f034974f"
#PV = "1.0.0"
#PR = "r0"

PACKAGE_ARCH = "${MIDDLEWARE_ARCH}"

S = "${WORKDIR}/git/libchronyctl"

inherit autotools pkgconfig

DEPENDS = "chrony"

FILES:${PN} += "${libdir}/libchronyctl.so.*"
FILES:${PN}-dev += "${libdir}/libchronyctl.la ${libdir}/libchronyctl.so \
                    ${includedir}/libchronyctl.h \
                    ${includedir}/addressing.h \
                    ${includedir}/candm.h"


RDEPENDS:${PN} = "chrony"
