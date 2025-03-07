SUMMARY = "Custom package group for RDK bits"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

inherit packagegroup

PACKAGES = "\
    packagegroup-rdk-generic-mediaserver \
    "

# Generic RDK components
RDEPENDS:packagegroup-rdk-generic-mediaserver = "\
    rmfstreamer \
    tenablehdcp \
    tr69hostif \
    hdparm \
    dibbler-client \
    procps \
    sys-utils \
    xfsprogs \
    ntp \
    power-state-monitor \
    "
