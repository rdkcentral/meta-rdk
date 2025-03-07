SUMMARY = "ucresolv library"
DESCRIPTION = "Recipe to build library for uclibc resolver missing features"
HOMEPAGE = "https://github.com/Comcast/libucresolv"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"

PACKAGE_ARCH = "${MIDDLEWARE_ARCH}"
SRCREV = "996c3778b14936c26b49e30f8dbb4933cb2df49a"
SRC_URI = "git://github.com/Comcast/libucresolv.git \
           file://0001-use-headers-from-sysroot.patch \
           file://0001-dn-expand-undefined-with-glibc-2.35.patch \
           "
PV ?= "1.0.0"
PR ?= "r1"
S = "${WORKDIR}/git"
DEPENDS = ""

inherit cmake pkgconfig
EXTRA_OECMAKE = "-DBUILD_TESTING=OFF -DBUILD_YOCTO=true"

FILES:${PN} += " \
	${libdir}/* \
"

FILES_SOLIBSDEV = ""
INSANE_SKIP:${PN} += "dev-so"

CFLAGS += "-I${S}/include/wcsmbs"
