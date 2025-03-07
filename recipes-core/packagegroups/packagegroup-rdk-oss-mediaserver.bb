SUMMARY = "Custom package group for OSS bits used in RDK"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

inherit packagegroup

PACKAGES = "\
    packagegroup-rdk-oss-mediaserver \
    "

# Opensource components used in RDK
RDEPENDS:packagegroup-rdk-oss-mediaserver = "\
    mongoose \
    libtinyxml \
    gptfdisk \
    xfsprogs-fsck \
    xfsprogs-mkfs \
    libhandle \
    net-snmp-client \
    net-snmp-server \
    net-snmp-mibs \
    net-snmp-server-snmpd \
    libsyswrapper \
    "
DEPENDS += " libsyswrapper"
