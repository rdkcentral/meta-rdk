SUMMARY = "Custom package group for OSS bits used in RDK"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

inherit packagegroup

PACKAGES = "\
    packagegroup-rdk-oss-mediaclient \
    "

# Opensource components used in RDK
RDEPENDS:packagegroup-rdk-oss-mediaclient = "\
    util-linux-sfdisk \
    "
RDEPENDS:packagegroup-rdk-oss-mediaclient_rpi += "\
    sysint \
    sysint-conf \
    "
