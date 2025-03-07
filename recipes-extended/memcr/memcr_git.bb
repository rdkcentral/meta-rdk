SUMMARY = "memcr memory hibernate/restore tool"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=0ba16794955006770904e8293abcbee5"

HOMEPAGE = "https://github.com/LibertyGlobal/memcr"

FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI = "git://git@github.com/LibertyGlobal/memcr.git;branch=main;protocol=ssh"
SRC_URI += " file://0001-RDK-54059-retry-ptrace-seize-on-EPERM.patch"
SRC_URI += " file://memcr.service"
SRC_URI += " file://0001-RDK-47153-Support-zstd-compression-and-option-to-set-dump-dir-.patch"

INSANE_SKIP:${PN} += "ldflags"

PV = "1.0+git${SRCPV}"
# Code base from 30.08.2024
SRCREV = "b58f2b8e26cab6b67eceaa36fd6ce5a6d04dcd28"

PACKAGE_ARCH = "${MIDDLEWARE_ARCH}"

DEPENDS += " util-linux-native lz4 openssl zstd"
RDEPENDS:${PN} = "libcrypto lz4 zstd"

S = "${WORKDIR}/git"

inherit systemd

SYSTEMD_SERVICE:${PN} = "memcr.service"
SYSTEMD_AUTO_ENABLE = "enable"

do_compile () {
	oe_runmake COMPRESS_LZ4=1 COMPRESS_ZSTD=1 CHECKSUM_MD5=1 ENCRYPT=1
}

do_install () {
	install -D -m 755 ${B}/memcr  ${D}${bindir}/memcr
	install -D -m 755 ${B}/parasite.bin  ${D}${bindir}/parasite.bin
	install -D -m 755 ${B}/memcr-client  ${D}${bindir}/memcr-client
	install -d ${D}${libdir}/memcr
	install -D -m 644 ${B}/libencrypt.so ${D}${libdir}/memcr/libencrypt.so
	if ${@bb.utils.contains('DISTRO_FEATURES','systemd','true','false',d)}; then
            install -d ${D}${systemd_unitdir}/system
            install -m 0644 ${WORKDIR}/memcr.service ${D}${systemd_unitdir}/system
	fi
}

FILES:${PN} += "${@bb.utils.contains('DISTRO_FEATURES','systemd','${systemd_unitdir}/system/memcr.service','',d)}"
FILES:${PN} += "${bindir}/memcr ${bindir}/parasite.bin ${bindir}/memcr-client"
FILES:${PN} += "${libdir}/memcr/libencrypt.so"
