SUMMARY = "C library for WebPA Data Model Parser (WDMP)"
HOMEPAGE = "https://github.com/Comcast/wdmp-c"
SECTION = "libs"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"

DEPENDS = "cjson cimplog"

SRCREV = "f9f687b6b4b10c2b72341e792a64334f0a409848"
SRC_URI = "git://github.com/xmidt-org/wdmp-c.git"
PV ?= "1.0.0"
PR ?= "r0"

PACKAGE_ARCH = "${MIDDLEWARE_ARCH}"

S = "${WORKDIR}/git"

inherit pkgconfig cmake

EXTRA_OECMAKE += "-DBUILD_TESTING=OFF -DBUILD_YOCTO=true"

LDFLAGS += "-lm"

# The libwdmp-c.so shared lib isn't versioned, so force the .so file into the
# run-time package (and keep it out of the -dev package).

FILES_SOLIBSDEV = ""
FILES:${PN} += "${libdir}/*.so"
