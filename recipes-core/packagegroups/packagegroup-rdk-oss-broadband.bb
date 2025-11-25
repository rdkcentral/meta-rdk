SUMMARY = "Custom package group for OSS bits used in RDK-B"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

PACKAGE_ARCH = "${TUNE_PKGARCH}"

inherit packagegroup

DEPENDS = "libnl"

PACKAGES = "\
    packagegroup-rdk-oss-broadband \
    "
# Opensource components used in RDK-B
RDEPENDS:packagegroup-rdk-oss-broadband = "\
    bridge-utils \
    curl \
    dibbler-client \
    dibbler-server \
    ${@bb.utils.contains("DISTRO_FEATURES", "kirkstone", "", "dhcp-client", d)} \
    ${@bb.utils.contains("DISTRO_FEATURES", "kirkstone", "", "dhcp-server",  d)} \
    ${@bb.utils.contains("DISTRO_FEATURES", "kirkstone", "",  "dhcp-server-config", d)} \
    dnsmasq \
    dropbear \
    expat \
    ez-ipupdate \
    fcgi \
    glib-2.0 \
    gnutls \
    igmpproxy \
    iksemel \
    iproute2 \
    iptables \
    jansson \
    libgcrypt \
    libgpg-error \
    libmtp \
    libnetfilter-queue \
    libnetfilter-conntrack \
    libnl \
    libpcre \
    libtinyxml \
    libupnp \
    libxml2 \
    ${@bb.utils.contains("DISTRO_FEATURES", "benchmark_enable", "", "lighttpd", d)} \
    log4c \
    logrotate \
    miniupnpd \
    mtd-utils-ubifs \
    neon \
    openssl \
    ${@bb.utils.contains("DISTRO_FEATURES", "webui_php", "php", "", d)} \
    ${@bb.utils.contains("DISTRO_FEATURES", "webui_php", "php-cli", "", d)} \
    ${@bb.utils.contains("DISTRO_FEATURES", "webui_php", "php-cgi", "", d)} \
    ${@bb.utils.contains("DISTRO_FEATURES", "bci", "php", "", d)} \
    ${@bb.utils.contains("DISTRO_FEATURES", "bci", "php-cli", "", d)} \
    ${@bb.utils.contains("DISTRO_FEATURES", "bci", "php-cgi", "", d)} \
    popt \
    ${@bb.utils.contains("DISTRO_FEATURES", "benchmark_enable", "", "quagga", d)} \
    smartmontools \
    spawn-fcgi \
    ssmtp \
    ${@bb.utils.contains('MACHINEOVERRIDES', 'morty', 'wireless-tools', 'iw', d)} \
    yajl \
    zlib \
    smcroute \
    libsyswrapper \
    ${@bb.utils.contains("DISTRO_FEATURES", "safec", "safec", "" , d)} \
    ${@bb.utils.contains("DISTRO_FEATURES", "rdkb_cellular_manager", "libqmi", "" , d)} \
    "
DEPENDS += " libsyswrapper"

RDEPENDS:packagegroup-rdk-oss-broadband += " ${@bb.utils.contains('DISTRO_FEATURES', 'btr_hciadv', 'bleadvhci', '', d)}"
RDEPENDS:packagegroup-rdk-oss-broadband += " ${@bb.utils.contains('DISTRO_FEATURES', 'snmppa', 'net-snmp-client net-snmp-server net-snmp-mibs net-snmp-server-snmpd', '', d)}"
RDEPENDS:packagegroup-rdk-oss-broadband += " ${@bb.utils.contains('DISTRO_FEATURES', 'rdk-oss-ssa', 'rdk-oss-ssa ecryptfs-utils', '', d)}"
RDEPENDS:packagegroup-rdk-oss-broadband += " ${@bb.utils.contains('DISTRO_FEATURES', 'gateway_manager', ' rsync ', '', d)}"

RDEPENDS:packagegroup-rdk-oss-broadband += " ${@bb.utils.contains('DISTRO_FEATURES', 'dac', 'dobby', '', d)}"
RDEPENDS:packagegroup-rdk-oss-broadband += " ${@bb.utils.contains('DISTRO_FEATURES', 'dac', 'dsm', '', d)}"
RDEPENDS:packagegroup-rdk-oss-broadband += " ${@bb.utils.contains("DISTRO_FEATURES", "benchmark_enable", "broadcom-bdmfshell", "", d)}"

