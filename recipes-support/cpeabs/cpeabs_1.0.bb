SUMMARY = "cpeabs library"
HOMEPAGE = "https://github.com/rdkcentral/cpeabs"
SECTION = "libs"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c66d00ff35dd49691ca2c06f8c370190"

PV = "2.0.0"
PR = "r0"
PACKAGE_ARCH = "${MIDDLEWARE_ARCH}"

DEPENDS = "cjson msgpack-c rbus wdmp-c cimplog"

SRCREV = "c017d418919ac548e5c3f2d2e534261502210720"

SRC_URI = "git://github.com/rdkcentral/cpeabs.git;protocol=https;branch=support/RDK-E"

S = "${WORKDIR}/git"

ASNEEDED = ""

inherit pkgconfig cmake

EXTRA_OECMAKE = "-DBUILD_TESTING=OFF -DBUILD_YOCTO=true"

LDFLAGS += "-lcjson -lmsgpackc -lwdmp-c -lcimplog -lrbus"

CFLAGS:append = " ${@bb.utils.contains("DISTRO_FEATURES", "WanFailOverSupportEnable", " -DWAN_FAILOVER_SUPPORTED ", " ", d)} "

CFLAGS:append = " ${@bb.utils.contains('DISTRO_FEATURES', 'mqttCM', '-DFEATURE_SUPPORT_MQTTCM', '', d)}"

CFLAGS:append = " \
        -DBUILD_YOCTO \
        -I${STAGING_INCDIR}/cjson \
        -I${STAGING_INCDIR}/rbus \
        -I${STAGING_INCDIR}/rtmessage \
        -I${STAGING_INCDIR}/wdmp-c \ 
        -I${STAGING_INCDIR}/cimplog \
        -fPIC \
        "
CFLAGS:append:dunfell = " -Wno-format-truncation -Wno-sizeof-pointer-memaccess"

# The libcpeabs.so shared lib isn't versioned, so force the .so file into the
# run-time package (and keep it out of the -dev package).

FILES_SOLIBSDEV = ""
FILES:${PN} += "${libdir}/*.so"

ASNEEDED_hybrid = ""
ASNEEDED_client = ""
