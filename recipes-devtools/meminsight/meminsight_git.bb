SUMMARY = "Memory insight utility and runner service"
SECTION = "console/utils"
DESCRIPTION = "meminsight: system/process memory statistics collection tool with systemd runner service."
LICENSE = "Apache-2.0"

LIC_FILES_CHKSUM = "file://${WORKDIR}/git/LICENSE;md5=1c020dfe1abb4e684874a44de1244c28"

SRC_URI = "${CMF_GITHUB_ROOT}/${BPN}.git;nobranch=1;protocol=${CMF_GIT_PROTOCOL}"

SRC_URI:append = " file://meminsight-runner.service \
                   file://meminsight-runner.path \
                   file://conf/client.conf \
                   file://conf/broadband.conf \
                   file://conf/client-path.conf \
                   file://conf/broadband-path.conf \
                   file://conf/broadband-rdm-path.conf \
                   file://start_meminsight.sh \
                   file://meminsight-upload.service \
                   file://meminsight-upload.path \
                   file://upload_MemReports.sh \
                   "

# May 25, 2026
SRCREV = "b90db0dffdf695eccb4e099f600c8bf75c148678"
PV = "1.1.1"
PR = "r0"
S = "${WORKDIR}/git"

PACKAGE_ARCH = "${MIDDLEWARE_ARCH}"

inherit autotools syslog-ng-config-gen systemd

# CFLAGS_append_broadband = ' -DDEVICE_INTERFACE_KEY=\\"ARM_INTERFACE\\" -DDEFAULT_OUT_DIR=\\"/nvram/meminsight\\"'

PACKAGECONFIG ??= "cjson"
PACKAGECONFIG[cjson] = "--enable-cjson,--disable-cjson"

EXTRA_OECONF += "${@bb.utils.contains('PACKAGECONFIG', 'cjson', '--enable-cjson', '--disable-cjson', d)}"
RDEPENDS:${PN} += "${@bb.utils.contains('PACKAGECONFIG', 'cjson', 'cjson', '', d)}"

SYSLOG-NG_FILTER = "meminsight"
SYSLOG-NG_SERVICE_meminsight = "meminsight-runner.service"
SYSLOG-NG_SERVICE_meminsight += " meminsight-upload.service"
SYSLOG-NG_DESTINATION_meminsight = "meminsight.log"
SYSLOG-NG_LOGRATE_meminsight = "medium"

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${B}/meminsight ${D}${bindir}/meminsight
    install -d ${D}${systemd_unitdir}/system
    install -m 0644 ${WORKDIR}/meminsight-runner.service ${D}${systemd_unitdir}/system/
    install -m 0644 ${WORKDIR}/meminsight-runner.path ${D}${systemd_unitdir}/system/
    install -m 0644 ${WORKDIR}/meminsight-upload.service ${D}${systemd_unitdir}/system/
    install -m 0644 ${WORKDIR}/meminsight-upload.path ${D}${systemd_unitdir}/system/
    install -d ${D}/lib/rdk
    install -m 0755 ${WORKDIR}/upload_MemReports.sh ${D}/lib/rdk/upload_MemReports.sh
    install -d ${D}${systemd_unitdir}/system/meminsight-runner.service.d
    install -d ${D}${systemd_unitdir}/system/meminsight-runner.path.d

}

do_install:append:client() {
    install -m 0644 ${WORKDIR}/conf/client.conf ${D}${systemd_unitdir}/system/meminsight-runner.service.d/
    if ${@bb.utils.contains('DISTRO_FEATURES', 'enable_xmeminsight', 'true', 'false', d)}; then
        install -m 0644 ${WORKDIR}/conf/client-path.conf ${D}${systemd_unitdir}/system/meminsight-runner.path.d/
    else
        install -m 0644 ${WORKDIR}/conf/broadband-rdm-path.conf ${D}${systemd_unitdir}/system/meminsight-runner.path.d/client-rdm-path.conf
        install -d ${D}/etc/rdm/post-services
        install -m 0755 ${WORKDIR}/start_meminsight.sh ${D}/etc/rdm/post-services/start_meminsight.sh
    fi
}

do_install:append:broadband() {
    install -m 0644 ${WORKDIR}/conf/broadband.conf ${D}${systemd_unitdir}/system/meminsight-runner.service.d/
    if ${@bb.utils.contains('DISTRO_FEATURES', 'enable_xmeminsight', 'true', 'false', d)}; then
        install -m 0644 ${WORKDIR}/conf/broadband-path.conf ${D}${systemd_unitdir}/system/meminsight-runner.path.d/
    else
        install -m 0644 ${WORKDIR}/conf/broadband-rdm-path.conf ${D}${systemd_unitdir}/system/meminsight-runner.path.d/
        install -d ${D}/etc/rdm/post-services
        install -m 0755 ${WORKDIR}/start_meminsight.sh ${D}/etc/rdm/post-services/start_meminsight.sh
    fi
}

SYSTEMD_SERVICE:${PN} = "meminsight-runner.path meminsight-upload.path"

FILES:${PN} += "${bindir}/meminsight"

FILES:${PN} += "${systemd_unitdir}/system/meminsight-runner.service"
FILES:${PN} += "${systemd_unitdir}/system/meminsight-runner.path"

FILES:${PN} += "${systemd_unitdir}/system/meminsight-upload.service"
FILES:${PN} += "${systemd_unitdir}/system/meminsight-upload.path"
FILES:${PN} += "/lib/rdk/upload_MemReports.sh"

FILES:${PN} += "${systemd_unitdir}/system/meminsight-runner.service.d/*.conf"
FILES:${PN} += "${systemd_unitdir}/system/meminsight-runner.path.d/*.conf"
