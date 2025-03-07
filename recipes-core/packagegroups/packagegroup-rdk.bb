SUMMARY = "Custom package group for RDK bits"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

inherit packagegroup

PACKAGES = "\
    packagegroup-rdk-generic \
    "

# Generic RDK components
RDEPENDS:packagegroup-rdk-generic = "\
    devicesettings \
    gst-plugins-rdk \
    iarmbus \
    iarmmgrs \
    "

RDEPENDS:packagegroup-rdk-generic:append:qemuall = " sysint"
RDEPENDS:packagegroup-rdk-media-common:append:qemuall = " sysint-conf"

# since we compile RDK component within qtwebkit (mediaplayersink) it
# is no longer a generic component anymore, and we need to make it
# part of RDK, since it won't compile in stand alone anymore..
RDEPENDS:packagegroup-rdk-generic += "\
    gst-plugins-base \
    gst-plugins-good \
    "
