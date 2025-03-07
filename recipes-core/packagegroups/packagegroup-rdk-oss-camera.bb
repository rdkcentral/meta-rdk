SUMMARY = "Custom package group for OSS bits used in RDK-C"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

inherit packagegroup

PACKAGES = "\
    packagegroup-rdk-oss-camera \
    "
# Opensource components used in RDK-C
RDEPENDS:packagegroup-rdk-oss-camera = "\
    bash \
    cryptsetup \
    curl \
    dante \
    dropbear \
    e2fsprogs \
    e2fsprogs-e2fsck \
    e2fsprogs-mke2fs \
    e2fsprogs-tune2fs \
    fcgi \
    glib-2.0 \
    gnutls \
    gssdp \
    iksemel \
    jansson \
    libgcrypt \
    libgpg-error \
    libpcre \
    libxml2 \
    lighttpd \
    log4c \
    logrotate \
    mtd-utils-ubifs \
    neon \
    popt \
    smartmontools \
    spawn-fcgi \
    yajl \
    dibbler-client \
    procps \
    dhcp-client \
    dhcp-server \
    dhcp-server-config \
    dnsmasq \
    iksemel \
    libgcrypt \
    libgpg-error \
    neon \
    openssl \
    wireless-tools \
    zlib \
    "
RDEPENDS:packagegroup-rdk-oss-camera:append:qemuall = " sysint "

