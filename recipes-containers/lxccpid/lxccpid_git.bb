SUMMARY = "lxccpid"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=175792518e4ac015ab6696d16c4f607e"

SRC_URI = "${CMF_GIT_ROOT}/rdk/components/generic/lxccpid;protocol=${CMF_GIT_PROTOCOL};branch=${CMF_GIT_MASTER_BRANCH};name=lxccpid"
SRCREV ?= "${AUTOREV}"
S = "${WORKDIR}/git"


inherit pkgconfig cmake

do_install() {
    install -d ${D}/${bindir}
    install -m 0755 ${B}/bin/lxccpid ${D}${bindir}/

    install -d ${D}/usr/share/src/lxccpid/
    install -m 0644 ${S}/lxccpid/pidmap.cpp ${D}/usr/share/src/lxccpid/

    install -d ${D}/usr/include/lxccpid/
    install -m 0644 ${S}/lxccpid/*.h ${D}/usr/include/lxccpid/
}

FILES:${PN} += "${bindir}"
FILES:${PN}-dev += "/usr/share/src/lxccpid/"
FILES:${PN}-dev += "/usr/include/lxccpid/"

