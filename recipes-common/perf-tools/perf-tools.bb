SUMMARY = "Performance Collection Script and Service"
DESCRIPTION = "Installs perf.sh, perf.conf, and systemd service"
LICENSE = "CLOSED"

SRC_URI += "file://perf.sh \
            file://perf.conf \
            file://perf-collector.service"
inherit systemd
do_install() {
    install -d ${D}/media/apps
    install -m 0755 ${WORKDIR}/perf.sh ${D}/media/apps/perf.sh
    install -m 0644 ${WORKDIR}/perf.conf ${D}/media/apps/perf.conf

    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/perf-collector.service ${D}${systemd_system_unitdir}/perf-collector.service
}

SYSTEMD_SERVICE:${PN} = "perf-collector.service"
SYSTEMD_AUTO_ENABLE:${PN} = "enable"
