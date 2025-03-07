SUMMARY = "Custom package group for RDK bits"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

inherit packagegroup
BLUEZ ?= "${@bb.utils.contains('DISTRO_FEATURES', 'bluetooth', bb.utils.contains('DISTRO_FEATURES', 'bluez5', 'bluez5', 'bluez4', d), '', d)}"

PACKAGES = "\
    packagegroup-rdk-media-common \
    "

# Generic RDK components
RDEPENDS:packagegroup-rdk-media-common = "\
    devicesettings \
    iarmbus \
    iarmmgrs \
    rdk-logger \
    gst-plugins-rdk \
    virtual/gst-plugins-playersinkbin \
    virtual/mfrlib \
    iarm-set-powerstate \
    iarm-query-powerstate \
    crashupload \
    crashupload-conf \
    key-simulator \
    tcpdump \
    iptables \
    ${@bb.utils.contains("DISTRO_FEATURES", "bluetooth", "${BLUEZ} bluetooth-core bluetooth-mgr virtual/vendor-media-utils", "", d)} \
    systemd-usb-support \
    nghttp2-server \
    nghttp2-common \
    stunnel \
    socat \
    ${@bb.utils.contains("DISTRO_FEATURES", "safec", "safec", "" , d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'rrd',' remotedebugger ',' ',d)} \
    rbus \
    telemetry \
    webconfig-framework \
    webcfg \
    dcmd \
    "
RDEPENDS:packagegroup-rdk-media-common:append:qemuall = " sysint "
RDEPENDS:packagegroup-rdk-media-common:append:qemuall = " sysint-conf "
RDEPENDS:packagegroup-rdk-media-common:append:mipsel = " dca "

IMAGE_INSTALL:append:rpi = " e2fsprogs-mke2fs "

#package for firebolt-test-client
RDEPENDS:packagegroup-rdk-media-common += " ${@bb.utils.contains('DISTRO_FEATURES', 'firebolt_test_client', 'firebolt-test-client', '', d)}"
