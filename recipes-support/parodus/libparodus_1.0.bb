SUMMARY = "C implementation of the WebPA client coordinator"
HOMEPAGE = "https://github.com/Comcast/parodus"
SECTION = "libs"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"

PACKAGE_ARCH = "${MIDDLEWARE_ARCH}"
DEPENDS = "cjson nopoll wrp-c wdmp-c trower-base64 nanomsg msgpack-c"

SRCREV = "8263bb06c8c16dc114c800a3d29d0c8252f15619"
SRC_URI = "git://github.com/Comcast/libparodus.git"
PV ?= "1.0.0"
PR ?= "r0"

S = "${WORKDIR}/git"

ASNEEDED = ""
inherit pkgconfig cmake

CFLAGS:append = " \
    -I${STAGING_INCDIR} \
    -I${STAGING_INCDIR}/cjson \
    -I${STAGING_INCDIR}/nopoll \
    -I${STAGING_INCDIR}/wdmp-c \
    -I${STAGING_INCDIR}/wrp-c \
    -I${STAGING_INCDIR}/nanomsg \
    -I${STAGING_INCDIR}/trower-base64 \
    "

EXTRA_OECMAKE += "-DBUILD_TESTING=OFF -DBUILD_YOCTO=true"

# The libparodus.so shared lib isn't versioned, so force the .so file into the
# run-time package (and keep it out of the -dev package).

FILES_SOLIBSDEV = ""
FILES:${PN} += "${libdir}/*.so"
