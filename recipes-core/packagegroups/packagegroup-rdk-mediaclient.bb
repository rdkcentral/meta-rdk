SUMMARY = "Custom package group for RDK bits"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

inherit packagegroup

PACKAGES = "\
    packagegroup-rdk-generic-mediaclient \
    "

# Generic RDK components
RDEPENDS:packagegroup-rdk-generic-mediaclient = "\
    dnsmasq \
    tr69hostif \
    tenablehdcp \
    netsrvmgr \
    util-linux-sfdisk \
    webkitbrowser-plugin \
    "

# since we compile RDK component within qtwebkit (mediaplayersink) it
# is no longer a generic component anymore, and we need to make it
# part of RDK, since it won't compile in stand alone anymore..
RDEPENDS:packagegroup-rdk-generic-mediaclient += "\
    ${@bb.utils.contains("DISTRO_FEATURES", "gstreamer1", "gstreamer1.0-plugins-base", "gst-plugins-base", d)} \
    ${@bb.utils.contains("DISTRO_FEATURES", "gstreamer1", "gstreamer1.0-plugins-good", "gst-plugins-good", d)} \
    ${@bb.utils.contains("DISTRO_FEATURES", "gstreamer1", "gstreamer1.0-plugins-bad", "gst-plugins-bad", d)} \
    "
