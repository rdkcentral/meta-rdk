SUMMARY = "Bluetooth SDK"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=175792518e4ac015ab6696d16c4f607e"

DEPENDS = "cmake-native breakpad rdk-logger breakpad-wrapper bluez5 glib-2.0 sdbus-c++"
RDEPENDS:${PN} = "rdk-logger bluez5 sdbus-c++"
SRC_URI = "git://github.com/rdkcentral/bluetooth-sdk.git;branch=feature/RDK-61473"
SRCREV = "578151e1e82190a1ce704e20961dcd7973eaf991"
S = "${WORKDIR}/git"

CFLAGS:append = " -I${STAGING_INCDIR} "
# LDFLAGS_append = " -lsdbus-c++  "


EXTRA_OECMAKE_BUILD = ""
EXTRA_OECMAKE += "-DWITH_CLI=ON -DCMAKE_BUILD_WITH_INSTALL_RPATH=ON -DCMAKE_INSTALL_RPATH=\$ORIGIN/../../lib/bluetoothsdk"

inherit cmake externalsrc breakpad-wrapper

do_install () {
    install -d ${D}${bindir}/bluetoothsdk
    install -d ${D}${libdir}/bluetoothsdk
    install -d ${D}${includedir}/bluetoothsdk/bluetooth/sdbus
    install -m 0755 ${B}/src/librdk_bluetooth.so ${D}${libdir}/bluetoothsdk/librdk_bluetooth.so
    install -m 0755 ${B}/client/btSdkCli ${D}${bindir}/bluetoothsdk/btSdkCli
    install -m 0755 ${S}/include/*.h ${D}${includedir}/bluetoothsdk/
    install -m 0755 ${S}/include/bluetooth/*.h ${D}${includedir}/bluetoothsdk/bluetooth/
    install -m 0755 ${S}/include/bluetooth/sdbus/*.h ${D}${includedir}/bluetoothsdk/bluetooth/sdbus/

}

FILES:${PN} = " ${bindir}/* ${libdir}/* "


PATH:prepend = "${STAGING_BINDIR_NATIVE}/:"
