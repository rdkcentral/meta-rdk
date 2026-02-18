SUMMARY = "bluetooth-core"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=175792518e4ac015ab6696d16c4f607e"

BLUEZ ?= "${@bb.utils.contains('DISTRO_FEATURES', 'bluetooth', bb.utils.contains('DISTRO_FEATURES', 'bluez5', 'bluez5', 'bluez4', d), '', d)}"

DEPENDS = "dbus ${BLUEZ} rdk-logger telemetry"
DEPENDS:append = " ${@bb.utils.contains('DISTRO_FEATURES', 'safec', ' safec', " ", d)}"
DEPENDS:append = "${@bb.utils.contains('DISTRO_FEATURES', 'gdbus_bluez5', 'glib-2.0-native', '', d)}"

RDEPENDS:${PN} = "dbus ${BLUEZ} rdk-logger telemetry"
PV = "1.0.8"
PR = "r2"

PACKAGE_ARCH = "${MIDDLEWARE_ARCH}"

SRCREV = "075eb565235edd9391a4208d424d544ad70190ba"
SRCREV_FORMAT = "bluetooth-core"

SRC_URI = "${CMF_GITHUB_ROOT}/bluetooth;${CMF_GITHUB_SRC_URI_SUFFIX}"
S = "${WORKDIR}/git"

CFLAGS:append = " ${@bb.utils.contains('DISTRO_FEATURES', 'safec',  ' `pkg-config --cflags libsafec`', '-fPIC', d)}"

CFLAGS:append = " ${@bb.utils.contains('DISTRO_FEATURES', 'safec', '', ' -DSAFEC_DUMMY_API', d)}"
LDFLAGS:append = " ${@bb.utils.contains('DISTRO_FEATURES', 'safec', ' `pkg-config --libs libsafec`', '', d)}"
CFLAGS:append:morty = " -DMORTY_BUILD"
CFLAGS:append:daisy = " -DMORTY_BUILD"

# BT unsupported gamepad feature enabled
ENABLE_UNSUPPORT_GAMEPAD = "--enable-unsupportedgamepad=${@bb.utils.contains('DISTRO_FEATURES', 'disable_unsupported_gamepad','no','yes',d)}"
EXTRA_OECONF += " ${ENABLE_UNSUPPORT_GAMEPAD} "

ENABLE_BTR_IFCE = "--enable-btr-ifce=${@bb.utils.contains('DISTRO_FEATURES', 'bluetooth', \
                                            bb.utils.contains('DISTRO_FEATURES', 'gdbus_bluez5', 'gdbus_bluez5', \
                                                bb.utils.contains('DISTRO_FEATURES', 'bluez5', 'bluez5', 'bluez4', d), d), 'none', d)}"
EXTRA_OECONF += "${ENABLE_BTR_IFCE}"

ENABLE_RDK_LOGGER = "--enable-rdk-logger=${@bb.utils.contains('RDEPENDS:${PN}', '${MLPREFIX}rdk-logger', 'yes', 'no', d)}"
EXTRA_OECONF += " ${ENABLE_RDK_LOGGER}"

ENABLE_SAFEC = "--enable-safec=${@bb.utils.contains('DISTRO_FEATURES', 'safec','yes', 'no', d)}"
EXTRA_OECONF += " ${ENABLE_SAFEC}"

inherit autotools pkgconfig coverity
