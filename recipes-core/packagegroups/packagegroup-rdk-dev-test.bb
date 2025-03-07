SUMMARY = "Custom package group for RDK bits"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

inherit packagegroup

PACKAGES = "\
    packagegroup-rdk-dev-test \
    "

# Opensource components used in RDK
RDEPENDS:packagegroup-rdk-dev-test = "\
    comcast-samplemedia \
    gstqamtunersrc \
    gdisplay \
    keyutil \
    gst-plugins-good-isomp4 \
    gst-plugins-good-autodetect \
    gst-plugins-good-audioparsers \
    gst-plugins-good-avi \
    gst-plugins-good-flv \
    perl \
    " 
