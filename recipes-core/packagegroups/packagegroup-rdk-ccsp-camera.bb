SUMMARY = "Custom package group for CCSP bits used in RDK-C"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

inherit packagegroup

PACKAGES = "\
    packagegroup-rdk-ccsp-camera \
"

# CCSP components used in RDK-C
RDEPENDS:packagegroup-rdk-ccsp-camera = "\
"
