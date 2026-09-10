SUMMARY = "WPE WebKit RDK backend"
HOMEPAGE = "https://github.com/WebPlatformForEmbedded"
SECTION = "wpe"
LICENSE = "BSD-2-Clause & Apache-2.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=ab5b52d145a58f5fcc0e2a531e7a2370"

DEPENDS += "libwpe glib-2.0"

PV ?= "0.5.0"
PR ?= "r0"

PACKAGE_ARCH = "${MIDDLEWARE_ARCH}"
PATCHTOOL = "git"

# Revision date: March 15, 2024
SRCREV = "6f53fce68d8e6895fd25a2b18c781bfbccdcf3b0"
BASE_URI ?= "git://github.com/WebPlatformForEmbedded/WPEBackend-rdk.git;protocol=https;branch=master"
SRC_URI = "${BASE_URI}"

SRC_URI += "file://comcast-Naive-gamepad-support.patch"
SRC_URI += "file://0001-Fix-browser-crash-when-the-compositor-is-not-created.patch"
SRC_URI += "file://0001-Send-SIGHUP-if-compositor-is-terminated.patch"
SRC_URI += "file://comcast-manette-gamepad-support.patch"
SRC_URI += "file://comcast-manette-gamepad-analog-button.patch"
SRC_URI += "file://comcast-manette-gamepad-digital-trigger-fix.patch"

S = "${WORKDIR}/git"

inherit cmake pkgconfig

WPE_BACKEND ?= "essos headless"
RDEPENDS:${PN}-platform-plugin:append = " essos"

PACKAGECONFIG ?= "${WPE_BACKEND}"
PACKAGECONFIG:append = " ${@bb.utils.contains('DISTRO_FEATURES', 'gamepad-using-libmanette', ' manettegamepad', ' gamepad', d)}"

PACKAGECONFIG[westeros] = "-DUSE_BACKEND_WESTEROS=ON -DUSE_KEY_INPUT_HANDLING_LINUX_INPUT=OFF,,wayland westeros libxkbcommon"
PACKAGECONFIG[essos] = "-DUSE_BACKEND_ESSOS=ON -DUSE_INPUT_LIBINPUT=OFF,-DUSE_BACKEND_ESSOS=OFF,essos libxkbcommon wayland"
PACKAGECONFIG[gamepad] = "-DUSE_GENERIC_GAMEPAD=ON,-DUSE_GENERIC_GAMEPAD=OFF,"
PACKAGECONFIG[headless] = "-DUSE_BACKEND_HEADLESS=ON -DUSE_INPUT_LIBINPUT=OFF,-DUSE_BACKEND_HEADLESS=OFF,"
PACKAGECONFIG[manettegamepad] = "-DUSE_LIBMANETTE_GAMEPAD=ON, -DUSE_LIBMANETTE_GAMEPAD=OFF, libmanette, libmanette-controllerdb"
EXTRA_OECMAKE += " \
    -DCMAKE_BUILD_TYPE=Release \
"

PROVIDES = "wpe-backend-rdk"
RPROVIDES:${PN} = "wpe-backend-rdk"

PACKAGES =+ "${PN}-platform-plugin"
FILES:${PN}-platform-plugin += "${libdir}/*.so ${bindir}/*"
INSANE_SKIP:${PN}-platform-plugin = "dev-so"

FILES_SOLIBSDEV = ""
SOLIBS = ".so"
INSANE_SKIP:${PN} += "dev-so"
FILES:${PN} += "${libdir}/*.so"
#We need the default package, even if empty for SDK
ALLOW_EMPTY:${PN}="1"
