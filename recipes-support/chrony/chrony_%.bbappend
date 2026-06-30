FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

RDEPENDS:${PN} += "bash"

SRC_URI += "file://chrony.conf \
            file://chronyd.service \
            file://rdk_chrony.conf \
            file://chrony-conf-update.sh \
           "

PACKAGECONFIG:remove = "editline"

do_install:append() {
    # Binaries
    install -m 0755 ${S}/chronyc ${D}${sbindir}
    install -d ${D}${base_libdir}/rdk

    #config File
    rm -rf ${D}${sysconfdir}/chrony.conf 
    install -m 0644 ${WORKDIR}/chrony.conf ${D}${sysconfdir}/
    install -m 0644 ${WORKDIR}/rdk_chrony.conf ${D}${sysconfdir}/
    install -m 0755 ${WORKDIR}/chrony-conf-update.sh ${D}${base_libdir}/rdk
    

    # service to start chrony
    rm -rf ${D}${systemd_unitdir}/system/chronyd.service
    install -m 0644 ${WORKDIR}/chronyd.service ${D}${systemd_unitdir}/system/
}


FILES:${PN} += "${sbindir}/chronyc"
CONFFILES:${PN} += "${sysconfdir}/chrony.conf"
CONFFILES:${PN} += "${sysconfdir}/rdk_chrony.conf"
FILES:${PN} += "${base_libdir}/rdk/chrony-conf-update.sh"
FILES:${PN} += "${systemd_unitdir}/system/chronyd.service"


inherit syslog-ng-config-gen
SYSLOG-NG_FILTER = "chronyd"
SYSLOG-NG_SERVICE_chronyd = "chronyd.service"
SYSLOG-NG_DESTINATION_chronyd = "chrony.log"
SYSLOG-NG_LOGRATE_chronyd = "low"

