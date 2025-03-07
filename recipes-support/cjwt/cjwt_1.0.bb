SUMMARY = "cjwt library"
DESCRIPTION = "Recipe to build c library for processing jwt token"
HOMEPAGE = "https://github.com/Comcast/cjwt"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"

PACKAGE_ARCH = "${MIDDLEWARE_ARCH}"
SRCREV = "2586a5ea4594f75fe5958c88acfdd7a7f635a067"
SRC_URI = "git://github.com/xmidt-org/cjwt.git"
PV ?= "1.0.0"
PR ?= "r1"
S = "${WORKDIR}/git"

DEPENDS = "trower-base64 cjson openssl"

LDFLAGS += "-lm -lcjson -ltrower-base64"

CFLAGS:append = " \
    -I${STAGING_INCDIR} \
    -I${STAGING_INCDIR}/cjson \
    -I${STAGING_INCDIR}/trower-base64 \
    "

CFLAGS:append:kirkstone = " -Wno-deprecated-declarations "

inherit cmake pkgconfig
EXTRA_OECMAKE = "-DBUILD_TESTING=OFF -DBUILD_YOCTO=true"

FILES:${PN} += " \
	${libdir}/* \
"

FILES_SOLIBSDEV = ""
INSANE_SKIP:${PN} += "dev-so"
