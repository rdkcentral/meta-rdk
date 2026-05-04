SUMMARY = "Bluetooth SDK"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=175792518e4ac015ab6696d16c4f607e"

DEPENDS = "cmake-native breakpad rdk-logger breakpad-wrapper bluez5 glib-2.0 sdbus-c++"
RDEPENDS:${PN} = "rdk-logger bluez5 sdbus-c++"
SRC_URI = "git://github.com/rdkcentral/bluetooth-sdk.git;branch=develop"
SRCREV = "cbb3bc96fd519e7a580f42d59e57050f64012024"
S = "${WORKDIR}/git"

CFLAGS:append = " -I${STAGING_INCDIR} "
# LDFLAGS_append = " -lsdbus-c++  "


EXTRA_OECMAKE_BUILD = ""

inherit cmake externalsrc breakpad-wrapper

do_install () {
    # install -d ${D}${bindir}/bluetoothsdk
    install -d ${D}${libdir}/bluetoothsdk
    install -d ${D}${includedir}/bluetoothsdk/bluetooth/sdbus

    install -m 0755 ${B}/src/librdk_bluetooth.so ${D}${libdir}/bluetoothsdk/librdk_bluetooth.so
    # currently no requirement for sdk cli
    install -m 0755 ${B}/client/btSdkCli ${D}${bindir}/bluetoothsdk/btSdkCli
    install -m 0755 ${S}/include/*.h ${D}${includedir}/bluetoothsdk/
    install -m 0755 ${S}/include/bluetooth/*.h ${D}${includedir}/bluetoothsdk/bluetooth/
    install -m 0755 ${S}/include/bluetooth/sdbus/*.h ${D}${includedir}/bluetoothsdk/bluetooth/sdbus/

}

FILES:${PN} = " ${bindir}/* ${libdir}/* "


PATH:prepend = "${STAGING_BINDIR_NATIVE}/:"
