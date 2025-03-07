SUMMARY = "C library for Web Routing Protocol (WRP)"
HOMEPAGE = "https://github.com/Comcast/wrp-c"
SECTION = "libs"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"

DEPENDS = "trower-base64 msgpack-c cimplog"

SRCREV = "9587e8db33dbbfcd9b78ef66cc2eaf16dfb9afcf"
SRC_URI = "git://github.com/xmidt-org/wrp-c.git"
PV ?= "1.0.0"
PR ?= "r0"

PACKAGE_ARCH = "${MIDDLEWARE_ARCH}"

S = "${WORKDIR}/git"

ASNEEDED = ""

inherit pkgconfig cmake

EXTRA_OECMAKE = "-DBUILD_TESTING=OFF -DBUILD_YOCTO=true"

LDFLAGS += "-lcimplog -lmsgpackc -ltrower-base64"

# The libwrp-c.so shared lib isn't versioned, so force the .so file into the
# run-time package (and keep it out of the -dev package).

FILES_SOLIBSDEV = ""
FILES:${PN} += "${libdir}/*.so"

ASNEEDED_hybrid = ""
ASNEEDED_client = ""
