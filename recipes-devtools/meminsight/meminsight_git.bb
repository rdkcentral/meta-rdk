SUMMARY = "Memory insight utility and runner service"
SECTION = "console/utils"
DESCRIPTION = "meminsight: system/process memory statistics collection tool with systemd runner service."
LICENSE = "Apache-2.0"

LIC_FILES_CHKSUM = "file://${WORKDIR}/git/LICENSE;md5=1b8525f92b462b86205ffaba159b4481"

SRC_URI = "git://github.com/rdkcentral/meminsight.git;branch=main;name=src;destsuffix=git"

SRC_URI_append = " file://meminsight-runner.service \
                   file://meminsight-runner.path \
                   file://conf/client.conf \
                   file://conf/broadband.conf \
                   file://conf/client-path.conf \
                   file://conf/broadband-path.conf \
                   file://conf/broadband-rdm-path.conf \
                   file://start_meminsight.sh \
                   "

# Feb 17, 2026
# v1.0.0
SRCREV = "a7e1e7375b5eaaa4cffd26fc2a40dbd359bc0b1f"
PV = "1.0.0"
PR = "r0"
S = "${WORKDIR}/git"

PACKAGE_ARCH = "${MIDDLEWARE_ARCH}"

inherit autotools systemd

# CFLAGS_append_broadband = ' -DDEVICE_IDENTIFIER=\\"erouter0\\" -DDEFAULT_OUT_DIR=\\"/nvram/meminsight\\"'

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${B}/meminsight ${D}${bindir}/meminsight
    install -d ${D}${systemd_unitdir}/system
    install -m 0644 ${WORKDIR}/meminsight-runner.service ${D}${systemd_unitdir}/system/
    install -m 0644 ${WORKDIR}/meminsight-runner.path ${D}${systemd_unitdir}/system/
    install -d ${D}${systemd_unitdir}/system/meminsight-runner.service.d
    install -d ${D}${systemd_unitdir}/system/meminsight-runner.path.d
}

do_install_append_client() {
    install -m 0644 ${WORKDIR}/conf/client.conf ${D}${systemd_unitdir}/system/meminsight-runner.service.d/
    install -m 0644 ${WORKDIR}/conf/client-path.conf ${D}${systemd_unitdir}/system/meminsight-runner.path.d/
}

do_install_append_broadband() {
    install -m 0644 ${WORKDIR}/conf/broadband.conf ${D}${systemd_unitdir}/system/meminsight-runner.service.d/
    if ${@bb.utils.contains('DISTRO_FEATURES', 'enable_xmeminsight', 'true', 'false', d)}; then
        install -m 0644 ${WORKDIR}/conf/broadband-path.conf ${D}${systemd_unitdir}/system/meminsight-runner.path.d/
    else
        install -m 0644 ${WORKDIR}/conf/broadband-rdm-path.conf ${D}${systemd_unitdir}/system/meminsight-runner.path.d/
        install -d ${D}/etc/rdm/post-services
        install -m 0755 ${WORKDIR}/start_meminsight.sh ${D}/etc/rdm/post-services/start_meminsight.sh
    fi
}

SYSTEMD_SERVICE_${PN} = "meminsight-runner.path"

FILES_${PN} += "${bindir}/meminsight"

FILES_${PN} += "${systemd_unitdir}/system/meminsight-runner.service"
FILES_${PN} += "${systemd_unitdir}/system/meminsight-runner.path"

FILES_${PN} += "${systemd_unitdir}/system/meminsight-runner.service.d/*.conf"
FILES_${PN} += "${systemd_unitdir}/system/meminsight-runner.path.d/*.conf"
