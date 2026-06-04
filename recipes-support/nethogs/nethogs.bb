SUMMARY = "NetHogs network monitor"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

SRC_URI = "git://github.com/raboof/nethogs.git;branch=master;protocol=https"

SRCREV = "da89c77c376039b7e1918ef4bcaebf5431bc1edc"
S = "${WORKDIR}/git"

inherit meson pkgconfig

DEPENDS += "libpcap ncurses"

FILES:${PN} += "${sbindir}/nethogs"

