SUMMARY = "RFC helper applications"
SECTION = "console/utils"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=bef3b9130aa5d626df3f7171f2dadfe2"

PACKAGECONFIG ??= "rfctool"
PACKAGECONFIG[rfctool] = "--enable-rfctool=yes"

SRC_URI = "${CMF_GITHUB_ROOT}/rfc;${CMF_GITHUB_SRC_URI_SUFFIX};name=rfc"
PACKAGE_ARCH = "${MIDDLEWARE_ARCH}"
S = "${WORKDIR}/git"

export cjson_CFLAGS = "-I$(PKG_CONFIG_SYSROOT_DIR)${includedir}/cjson"
export cjson_LIBS = "-lcjson"

DEPENDS="cjson curl commonutilities libsyswrapper iarmmgrs-hal-headers rdkcertconfig"
DEPENDS:append:broadband += " rbus"
export rbus_CFLAGS = "-I$(PKG_CONFIG_SYSROOT_DIR)${includedir}/rbus"
export rbus_LIBS = "-lrbus"

EXTRA_OEMAKE += "-e MAKEFLAGS="
EXTRA_OECONF:append = " --enable-iarmbus=yes --enable-tr69hostif=yes"
EXTRA_OECONF:append:broadband = " --enable-rdkb=yes --enable-tr181set=yes"
EXTRA_OECONF += " --enable-rdkcertselector=yes"
EXTRA_OEMAKE += "LIBS='-lsafec'"

DEPENDS_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'safec', ' safec', " ", d)}"
CFLAGS_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'safec',  ' `pkg-config --cflags libsafec`', '-fPIC', d)}"
CFLAGS_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'safec', '', ' -DSAFEC_DUMMY_API', d)}"
LDFLAGS_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'safec', ' `pkg-config --libs libsafec`', '', d)}"
LDFLAGS_append_morty = " ${@bb.utils.contains('DISTRO_FEATURES', 'safec', ' -Wl,--no-as-needed -lsafec-3.5.1 -Wl,--as-needed', '', d)}"

inherit autotools pkgconfig coverity

CFLAGS += " -Wall -Werror -Wextra "
CFLAGS += "${@bb.utils.contains('DISTRO_FEATURES', 'enable_maintenance_manager', '-DEN_MAINTENANCE_MANAGER -I${STAGING_INCDIR}/rdk/iarmmgrs-hal ', '', d)}"
CXXFLAGS += " -Wall -Werror"

do_install:append () {
	install -d ${D}${base_libdir}/rdk
        install -d ${D}${sysconfdir}

        install -m 0644 ${S}/rfc.properties ${D}${sysconfdir}/rfc.properties
}

RDEPENDS:${PN} += "busybox"

DEPENDS += "${@bb.utils.contains('DISTRO_FEATURES', 'gtestapp', ' gtest gmock', '', d)}"
PACKAGES =+ "${@bb.utils.contains('DISTRO_FEATURES', 'gtestapp', '${PN}-gtest', '', d)}"

FILES:${PN}-gtest = "\
    ${@bb.utils.contains('DISTRO_FEATURES', 'gtestapp', '${bindir}/rfc_gtest.bin', '', d)} \
"
FILES:${PN} += "${bindir}/rfctool"
FILES:${PN} += "${base_libdir}/*"
FILES:${PN} += "${sysconfdir}/*"

DOWNLOAD_APPS="${@bb.utils.contains('DISTRO_FEATURES', 'gtestapp', 'gtestapp-rfc', '', d)}"
inherit comcast-package-deploy
CUSTOM_PKG_EXTNS="${@bb.utils.contains('DISTRO_FEATURES', 'gtestapp', 'gtest', '', d)}"
SKIP_MAIN_PKG="${@bb.utils.contains('DISTRO_FEATURES', 'gtestapp', 'yes', 'no', d)}"
DOWNLOAD_ON_DEMAND="${@bb.utils.contains('DISTRO_FEATURES', 'gtestapp', 'yes', 'no', d)}"
