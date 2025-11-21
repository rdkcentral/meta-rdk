SUMMARY = "This recipe compiles Telemetry"
SECTION = "console/utils"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=175792518e4ac015ab6696d16c4f607e"

SRC_URI = "${CMF_GITHUB_ROOT}/telemetry;${CMF_GITHUB_SRC_URI_SUFFIX}"
PACKAGE_ARCH = "${MIDDLEWARE_ARCH}"

DEPENDS += "curl cjson glib-2.0 breakpad-wrapper rbus libsyswrapper libunpriv"
DEPENDS += "rdk-logger"

RDEPENDS:${PN} += "curl cjson glib-2.0 rbus"


PV ?= "1.0.1"
PR ?= "r0"

S = "${WORKDIR}/git"

#compiler warnings were fixed as part of RDK-55297
#CFLAGS += " -Wall -Werror -Wextra -Wno-unused-parameter -Wno-pointer-sign -Wno-sign-compare -Wno-enum-compare -Wno-type-limits -Wno-enum-conversion -Wno-format-truncation"
CFLAGS += " -Wall -Werror -Wextra"

inherit pkgconfig autotools systemd ${@bb.utils.contains("DISTRO_FEATURES", "kirkstone", "python3native", "pythonnative", d)} breakpad-logmapper

CFLAGS += " -DDROP_ROOT_PRIV -DENABLE_MTLS "

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

SRCREV = "0d73470cc038f1e47ffee483a7d05e51bf88751e"

PV = "1.6.4"
PR = "r0"
PACKAGE_ARCH = "${MIDDLEWARE_ARCH}"
