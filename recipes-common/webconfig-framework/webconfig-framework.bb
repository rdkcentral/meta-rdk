SUMMARY = "libwebconfig_framework component"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=bc21fa26f9718980827123b8b80c0ded"

DEPENDS = "rbus"
DEPENDS:append = " ${@bb.utils.contains('DISTRO_FEATURES', 'safec', ' safec', " ", d)}"
DEPENDS:class-native = ""

RDEPENDS:${PN}:append = " bash"
RDEPENDS:${PN}:remove_morty = "bash"

SRC_URI = "${CMF_GITHUB_ROOT}/WebconfigFramework;${CMF_GITHUB_SRC_URI_SUFFIX}"

SRCREV_FORMAT = "WebconfigFramework"
PV = "1.0.0"
PR = "r0"

PACKAGE_ARCH = "${MIDDLEWARE_ARCH}"

S = "${WORKDIR}/git"

inherit autotools systemd pkgconfig

CFLAGS += " \
    -D_GNU_SOURCE -D__USE_XOPEN \
    -I${STAGING_INCDIR}/dbus-1.0 \
    -I${STAGING_LIBDIR}/dbus-1.0/include \
    -I${STAGING_INCDIR}/rbus \
    -I${STAGING_INCDIR}/rtmessage \
    "

CFLAGS += " -Wall -Werror -Wextra "

CFLAGS:append = " -Wno-restrict -Wno-format-truncation -Wno-format-overflow -Wno-cast-function-type -Wno-unused-function -Wno-implicit-fallthrough "

CFLAGS:append = " ${@bb.utils.contains('DISTRO_FEATURES', 'webconfig_bin', '-DWEBCONFIG_BIN_SUPPORT', '', d)}"

LDFLAGS += " \
    -lrbuscore \
    -lrtMessage \
    -lrbus \
    "

do_configure:class-native () {
    echo "Configure is skipped"
}

do_compile:class-native () {
    echo "Compile is skipped"
}

do_install:append:class-target () {
    install -d ${D}/usr/include/
    install -m 644 ${S}/include/*.h ${D}/usr/include/

}

do_install:class-native () {
    echo "Compile is skipped"
}

FILES:${PN} += "${libdir}/*.so"

BBCLASSEXTEND = "native"
