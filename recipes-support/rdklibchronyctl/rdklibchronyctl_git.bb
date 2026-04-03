SUMMARY = "Chrony Control Shared Library"
DESCRIPTION = "Builds and installs libchronyctl and headers"
#LICENSE = "Apache-2.0"
#LIC_FILES_CHKSUM = "file://rdkchronylibctl/files/LICENSE;md5=<actual_license_md5>"
LICENSE - "CLOSED"

SRC_URI = "git://github.com/rdkcentral/time-utils.git;branch=develop;protocol=https"
SRCREV_time-utils = "e793b6419293bce3170405d12574436153df4dd6"


PACKAGE_ARCH = "${MIDDLEWARE_ARCH}"

S = "${WORKDIR}/git" # Adjust to top-level if configure.ac is here

# If configure.ac is under rdkchronylibctl/files, then:
# S = "${WORKDIR}/git/rdkchronylibctl/files"

inherit autotools pkgconfig

DEPENDS = ""

# If you only want to build this component and not the whole repo, set a suitable S above and restrict to that path!

FILES:${PN} += "${libdir}/libchronyctl.so.*"
FILES:${PN}-dev += "${libdir}/libchronyctl.la ${libdir}/libchronyctl.so \
                    ${includedir}/chronyctl/libchronyctl.h"

EXTRA_OECONF = ""
EXTRA_OEMAKE = ""

RDEPENDS:${PN} = "chrony"
