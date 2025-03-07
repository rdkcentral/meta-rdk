SUMMARY = "RDK OSS SSA Libraries and artifacts"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=19a2b3c39737289f92c7991b16599360"

SRC_URI = "${CMF_GIT_ROOT}/rdk/components/generic/rdkssa;protocol=${CMF_GIT_PROTOCOL};branch=${CMF_GIT_BRANCH};name=rdk-oss-ssa"

PV = "${RDK_RELEASE}+git${SRCPV}"
SRCREV_rdk-oss-ssa = "698fc07ad2772c77a005bda616fbaf64c632d5ed"
PROVIDES = "rdk-oss-ssa"
RPROVIDES:${PN} = "rdk-oss-ssa"
SRCREV_FORMAT = "rdk-oss-ssa"
S = "${WORKDIR}/git"

DEPENDS = " ecryptfs-utils keyutils safec-common-wrapper"
DEPENDS:append = " ${@bb.utils.contains('DISTRO_FEATURES', 'safec', ' safec', " ", d)}"
inherit pkgconfig autotools systemd

INCLUDE_DIRS = " \
    -I${STAGING_INCDIR} \
   "

CFLAGS += "${INCLUDE_DIRS} "
CPPFLAGS += " ${INCLUDE_DIRS} "
LDFLAGS += " -pthread -ldl"
CFLAGS:append = " ${@bb.utils.contains('DISTRO_FEATURES', 'safec',  ' `pkg-config --cflags libsafec`', '-fPIC', d)}"
CFLAGS:append = " ${@bb.utils.contains('DISTRO_FEATURES', 'safec', '', ' -DSAFEC_DUMMY_API', d)}"
LDFLAGS:append = " ${@bb.utils.contains('DISTRO_FEATURES', 'safec', ' `pkg-config --libs libsafec`', '', d)}"

#By default, RDKSSA Unit Test cases are disabled.
#Use "RDKSSA_UT_ENABLED=yes" to enable the RDKSSA Unit test cases
export RDKSSA_UT_ENABLED="no"

do_install:prepend() {

    install -d ${D}${includedir}
    install -D -m 0644 ${S}/ssa_top/ssa_oss/ssa_common/rdkssa.h ${D}${includedir}/

    install -d ${D}${systemd_unitdir}/system
    install -D -m 0644 ${S}/ssa_top/ssa_oss/ssa_common/providers/Mount/scripts/rdk-oss-ssa-ecfsinit.service ${D}${systemd_unitdir}/system/rdk-oss-ssa-ecfsinit.service

    install -d ${D}${bindir}
    install -D -m 0755 ${S}/ssa_top/ssa_oss/ssa_common/providers/Mount/scripts/ecfsMount ${D}${bindir}/

    install -d ${D}${sysconfdir}
    install -D -m 0644 ${S}/ssa_top/ssa_oss/ssa_common/providers/Mount/scripts/ecfs-mount-sample-dummy-key ${D}${sysconfdir}/

}

SYSTEMD_SERVICE:${PN} = " rdk-oss-ssa-ecfsinit.service"
FILES:${PN}:append = " ${systemd_unitdir}/system/*"

FILES:${PN} += "${bindir}/*"
FILES:${PN} += "${base_libdir}/*"
FILES:${PN} += "${sysconfdir}/*"


