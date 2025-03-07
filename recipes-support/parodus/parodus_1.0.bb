SUMMARY = "parodus client library"
SECTION = "libs"
DESCRIPTION = "C client library for parodus"
HOMEPAGE = "https://github.com/Comcast/parodus"

DEPENDS = "cjson nopoll wrp-c wdmp-c trower-base64 nanomsg msgpack-c rdk-logger log4c util-linux cjwt ucresolv curl"
DEPENDS:append = "${@bb.utils.contains("DISTRO_FEATURES", "seshat", " libseshat ", " ", d)}"
DEPENDS:append = "${@bb.utils.contains("DISTRO_FEATURES", "webconfig_bin", " rbus ", " ", d)}"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"

PACKAGE_ARCH = "${MIDDLEWARE_ARCH}"
SRCREV= "8d4c61a16faea4fc629b570542e7f29ccc27a5fa"
#SRCREV Upated from check-ins a96ca96858535645c8fd8a12227d2e00b6bcef9e, 84a7521538b850f569cbbe9f12eb777875649822, c4c41bde1abd8bdff99fb1acba0a81316b16dce6, 963a1664cc327c62c4a2b6eee1c48b55ec4e0f77
#PR version not changed

SRC_URI = " \
    git://github.com/xmidt-org/parodus.git \
    "

RDEPENDS:${PN} += "util-linux-uuidgen"

RDEPENDS:${PN}:append = " bash"
RDEPENDS:${PN}:remove_morty = "bash"

PV ?= "1.0.1"
PR ?= "r0"
S = "${WORKDIR}/git"

ASNEEDED = ""
LDFLAGS += "-lm -llog4c -lrdkloggers -lcjson -lnopoll -lwrp-c -lwdmp-c -lmsgpackc -ltrower-base64 -luuid -lnanomsg -lcjwt -lucresolv -lresolv"
LDFLAGS:append = "${@bb.utils.contains("DISTRO_FEATURES", "seshat", " -llibseshat ", " ", d)}"
LDFLAGS:append = "${@bb.utils.contains("DISTRO_FEATURES", "webconfig_bin", " -lrbus ", " ", d)}"

CFLAGS:append = " \
    -I${STAGING_INCDIR} \
    -I${STAGING_INCDIR}/cjson \
    -I${STAGING_INCDIR}/nopoll \
    -I${STAGING_INCDIR}/wdmp-c \
    -I${STAGING_INCDIR}/wrp-c \
    -I${STAGING_INCDIR}/cimplog \
    -I${STAGING_INCDIR}/nanomsg \
    -I${STAGING_INCDIR}/trower-base64 \
    -I${STAGING_INCDIR}/cjwt \
    -I${STAGING_INCDIR}/ucresolv \
    -DFEATURE_DNS_QUERY \
    "
CFLAGS:append = " -DINCLUDE_BREAKPAD " 
CFLAGS:append = "${@bb.utils.contains("DISTRO_FEATURES", "seshat", "-I${STAGING_INCDIR}/libseshat ", " ", d)}"
CFLAGS:append = "${@bb.utils.contains("DISTRO_FEATURES", "seshat", "-DENABLE_SESHAT ", " ", d)}"
CFLAGS:append = "${@bb.utils.contains("DISTRO_FEATURES", "webconfig_bin", " -I${STAGING_INCDIR}/rbus ", " ", d)}"

inherit pkgconfig cmake
EXTRA_OECMAKE = "-DBUILD_TESTING=OFF -DBUILD_YOCTO=true -DFEATURE_DNS_QUERY=true"
EXTRA_OECMAKE:append = "${@bb.utils.contains("DISTRO_FEATURES", "seshat", " -DENABLE_SESHAT=true", " ", d)}"
EXTRA_OECMAKE:append = "${@bb.utils.contains("DISTRO_FEATURES", "webconfig_bin", " -DENABLE_WEBCFGBIN=true", " ", d)}"
EXTRA_OECMAKE:append = "${@bb.utils.contains("DISTRO_FEATURES", "WanFailOverSupportEnable", " -DWAN_FAILOVER_SUPPORTED=true", " ", d)}"

do_compile:prepend() {
	sed -i 's/-Werror -Wall/-Wno-error=all/g' ${S}/CMakeLists.txt
}

do_install:append() {
	install -d ${D}${sysconfdir}/ssl/certs
	install -m 0644 ${S}/tests/webpa-rs256.pem ${D}${sysconfdir}/ssl/certs
}

FILES:${PN} += " \
        ${bindir}/* \
	${sysconfdir}/ssl/certs/* \
"
