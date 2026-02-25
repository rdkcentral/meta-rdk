SUMMARY = "secure wrapper for system calls"
LICENSE = "Apache-2.0"

LIC_FILES_CHKSUM = "file://LICENSE;md5=175792518e4ac015ab6696d16c4f607e"

PV = "1.3.3"
PR = "r1"
PACKAGE_ARCH = "${MIDDLEWARE_ARCH}"
S = "${WORKDIR}/git"
DEPENDS += "rdk-logger"
SRCREV = "69fab9ab4403b7cda68af1d6a095b2cf5749c84f"
SRC_URI = "${CMF_GITHUB_ROOT}/libSyscallWrapper;${CMF_GITHUB_SRC_URI_SUFFIX};name=libsyswrapper"
#EXTRA_OECONF += "--enable-testapp"
CFLAGS:append = " -Wall -Werror"
CXXFLAGS:append = " -Wall -Werror"

DEBIAN_NOAUTONAME:${PN} = "1"

inherit autotools pkgconfig coverity

