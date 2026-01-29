SUMMARY = "This recipe compiles Telemetry"
SECTION = "console/utils"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=175792518e4ac015ab6696d16c4f607e"

SRC_URI = "git://github.com/rdkcentral/telemetry.git;protocol=https;branch=topic/RDK-60318"

SRCREV = "b1e8760cd4c74957df3ebc811a795b18e5b9fe61"

PACKAGE_ARCH = "${MIDDLEWARE_ARCH}"

DEPENDS += "curl cjson glib-2.0 breakpad-wrapper rbus libsyswrapper libunpriv dbus"
DEPENDS += "rdk-logger"

RDEPENDS:${PN} += "curl cjson glib-2.0 rbus dbus"


PV = "1.7.4"
PR = "r0"

S = "${WORKDIR}/git"

#compiler warnings were fixed as part of RDK-55297
#CFLAGS += " -Wall -Werror -Wextra -Wno-unused-parameter -Wno-pointer-sign -Wno-sign-compare -Wno-enum-compare -Wno-type-limits -Wno-enum-conversion -Wno-format-truncation"
CFLAGS += " -Wall -Werror -Wextra"
#FIXME, this is temporary workaround for broadband. It has to be verified and remove these suppression flags
CFLAGS:append:broadband += " -DRDK_LOGGER -Wno-sign-compare -Wno-unused-parameter -Wno-pointer-sign"

inherit pkgconfig autotools systemd ${@bb.utils.contains("DISTRO_FEATURES", "kirkstone", "python3native", "pythonnative", d)} breakpad-logmapper

CFLAGS += " -DDROP_ROOT_PRIV -DENABLE_MTLS -I${STAGING_INCDIR}/dbus-1.0 -I${STAGING_LIBDIR}/dbus-1.0/include "

LDFLAGS:append = " \
        -lbreakpadwrapper \
        -lpthread \
        -lstdc++ \
        -lsecure_wrapper \
        "
LDFLAGS:append = " \
        -lprivilege \
      "

CXXFLAGS += "-DINCLUDE_BREAKPAD"

do_install:append () {
    install -d ${D}/usr/include/
    install -d ${D}/lib/rdk/
    install -d ${D}${systemd_unitdir}/system
    install -m 644 ${S}/include/telemetry_busmessage_sender.h ${D}/usr/include/
    install -m 644 ${S}/include/telemetry2_0.h ${D}/usr/include/
    install -m 0755 ${S}/source/commonlib/t2Shared_api.sh ${D}/lib/rdk
    rm -fr ${D}/usr/lib/libtelemetry_msgsender.la 
    rm -rf ${D}${bindir}/t2rbusMethodSimulator
}

FILES:${PN} = "\
    ${bindir}/telemetry2_0 \
    ${bindir}/telemetry2_0_client \
    ${systemd_unitdir}/system \
"
FILES:${PN} += "${libdir}/*.so*"
FILES:${PN} += "/lib/rdk/*"
FILES:${PN}-dbg += "${sysconfdir}/logrotate/*"

FILES_SOLIBSDEV = ""
INSANE_SKIP:${PN} += "dev-so"

PACKAGES =+ "${@bb.utils.contains('DISTRO_FEATURES', 'gtestapp', '${PN}-gtest', '', d)}"

FILES:${PN}-gtest = "\
    ${@bb.utils.contains('DISTRO_FEATURES', 'gtestapp', '${bindir}/telemetry_gtest.bin ${bindir}/xconfclient_gtest.bin ${bindir}/t2parser_gtest.bin ${bindir}/reportgen_gtest.bin ${bindir}/scheduler_gtest.bin', '', d)} \
"

DOWNLOAD_APPS="${@bb.utils.contains('DISTRO_FEATURES', 'gtestapp', 'gtestapp-telemetry', '', d)}"
inherit comcast-package-deploy
CUSTOM_PKG_EXTNS="${@bb.utils.contains('DISTRO_FEATURES', 'gtestapp', 'gtest', '', d)}"
SKIP_MAIN_PKG="${@bb.utils.contains('DISTRO_FEATURES', 'gtestapp', 'yes', 'no', d)}"
DOWNLOAD_ON_DEMAND="${@bb.utils.contains('DISTRO_FEATURES', 'gtestapp', 'yes', 'no', d)}"

# Breakpad processname and logfile mapping
BREAKPAD_LOGMAPPER_PROCLIST = "telemetry2_0"
BREAKPAD_LOGMAPPER_LOGLIST = "telemetry2_0.txt.0"
