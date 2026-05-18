SUMMARY = "Chrony Control Shared Library"
DESCRIPTION = "Builds and installs libchronyctl and headers"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${WORKDIR}/git/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

SRC_URI = "git://github.com/rdkcentral/time-utils.git;branch=topic/chronyctl;protocol=https"
SRCREV = "b25a38d74ff9baa6c2ee52d65a3aa5a7d7492b8f"

PACKAGE_ARCH = "${MIDDLEWARE_ARCH}"

S = "${WORKDIR}/git/libchronyctl"

inherit autotools pkgconfig

DEPENDS = "chrony"

FILES:${PN} += "${libdir}/libchronyctl.so.*"
FILES:${PN}-dev += "${libdir}/libchronyctl.la ${libdir}/libchronyctl.so \
                    ${includedir}/libchronyctl.h \
                    ${includedir}/addressing.h \
                    ${includedir}/candm.h"

EXTRA_OECONF = ""
EXTRA_OEMAKE = ""

RDEPENDS:${PN} = "chrony"
