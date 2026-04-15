SUMMARY = "This recipe compiles Telemetry"
SECTION = "console/utils"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=175792518e4ac015ab6696d16c4f607e"

SRCREV = "6ec93e1ec9d22bb2e14a2f771b648464f08a71ef"
SRC_URI = "${CMF_GITHUB_ROOT}/telemetry;${CMF_GITHUB_SRC_URI_SUFFIX}"

DEPENDS += "curl cjson glib-2.0 breakpad-wrapper rbus libsyswrapper libunpriv webconfig-framework"
DEPENDS += "rdk-logger"

RDEPENDS:${PN} += "curl cjson glib-2.0 rbus"


PV = "1.8.5"
PR = "r0"

S = "${WORKDIR}/git"

# Copy stub webconfig_framework.h header to source tree

#compiler warnings were fixed as part of RDK-55297
#CFLAGS += " -Wall -Werror -Wextra -Wno-unused-parameter -Wno-pointer-sign -Wno-sign-compare -Wno-enum-compare -Wno-type-limits -Wno-enum-conversion -Wno-format-truncation"
# Suppress all warnings as errors
CFLAGS += " -Wall -Wextra -Wno-error"
# Add include path for stub webconfig_framework.h
CFLAGS:append = " -I${S}/include"
#FIXME, this is temporary workaround for broadband. It has to be verified and remove these suppression flags
CFLAGS:append:broadband += " -DRDK_LOGGER -Wno-sign-compare -Wno-unused-parameter -Wno-pointer-sign"

# Use make -e to force environment variables to override Makefile settings
EXTRA_OEMAKE = "-e"

inherit pkgconfig autotools systemd ${@bb.utils.contains("DISTRO_FEATURES", "kirkstone", "python3native", "pythonnative", d)} breakpad-logmapper

CFLAGS += " -DDROP_ROOT_PRIV -DENABLE_MTLS "

CFLAGS:append = " -Wno-unused-result -Wno-format-security"
CXXFLAGS:append = " -Wno-unused-result -Wno-format-security"

CFLAGS:remove = "-Werror"
CXXFLAGS:remove = "-Werror"

CFLAGS:remove = "-Werror=format-security"
CXXFLAGS:remove = "-Werror=format-security"

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
