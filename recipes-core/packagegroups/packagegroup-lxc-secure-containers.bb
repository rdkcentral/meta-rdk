SUMMARY = "Custom package group for containers"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${MANIFEST_PATH_META_RDK}/licenses/Apache-2.0;md5=3b83ef96387f14655fc854ddc3c6bd57"

inherit packagegroup

PACKAGES = "\
    packagegroup-lxc-secure-containers \
    "

RDEPENDS:packagegroup-lxc-secure-containers = "\
    libcap \
    lxc \
    lxccpid \
    "
