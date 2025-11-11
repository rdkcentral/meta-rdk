SUMMARY = "Downloadable Software Modules"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=50e2d278b66b3b7b20bc165c146a2b58"

S = "${WORKDIR}/git"
SRC_URI = "${CMF_GITHUB_ROOT}/DSM;protocol=${CMF_GIT_PROTOCOL};branch=${CMF_GITHUB_MAIN_BRANCH} \
           file://dsm.config \
           file://dsm.service "

SRCREV = "c3cfd0a9d1ee1e9c2f2e8db4348b314aa32439bc"
SRCREV_kirkstone = "be204cb7f1e46ad6b66489172010eee80967b4be"
DEPENDS_append_kirkstone = " dobby "

EXTRA_OECMAKE =  " -DENABLE_RBUS_PROVIDER=ON"

inherit pkgconfig cmake systemd

DEPENDS += "rbus"
DEPENDS += "dobby"

RDEPENDS_${PN} += "rbus"
RDEPENDS_${PN} += "dobby"

SYSTEMD_AUTO_ENABLE_${PN} = "enable"
SYSTEMD_SERVICE_${PN} = "dsm.service"

FILES_${PN} += " ${systemd_unitdir}/system/dsm.service \
                 ${sysconfdir}/dsm.config \
                 /home/root/destination "

OECMAKE_CXX_FLAGS += " -I${STAGING_INCDIR}/rbus "
OECMAKE_CXX_FLAGS += "-I${STAGING_INCDIR}"

SYSTEMD_AUTO_ENABLE_${PN} = "enable"
SYSTEMD_SERVICE_${PN} = " dsm.service "

do_install_append() {
    install -d ${D}${sysconfdir} 
    install -D -m 644 ${WORKDIR}/dsm.config ${D}${sysconfdir}
    install -d ${D}/${systemd_unitdir}/system
    install -m 0644 ${WORKDIR}/dsm.service ${D}/${systemd_unitdir}/system/dsm.service
    install -d ${D}/home/root/destination
}
