FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

RDEPENDS:${PN} += "bash"

SRC_URI += "file://chrony.conf \
            file://chronyd.service \
            file://rdk_chrony.conf \
            file://chrony-sync-notify.sh \
            file://chrony-sync-notify.service \
            file://chrony-conf-update.sh \
            file://chrony-tracking.timer \
            file://chrony-tracking.service \
            file://chrony_tracking.sh \
           "

do_install:append() {
    # Binaries
    install -m 0755 ${S}/chronyc ${D}${sbindir}
    install -d ${D}${base_libdir}/rdk

    #config File
    rm -rf ${D}${sysconfdir}/chrony.conf 
    install -m 0644 ${WORKDIR}/chrony.conf ${D}${sysconfdir}/
    install -m 0644 ${WORKDIR}/rdk_chrony.conf ${D}${sysconfdir}/
    install -m 0755 ${WORKDIR}/chrony-sync-notify.sh ${D}${base_libdir}/rdk
    install -m 0755 ${WORKDIR}/chrony-conf-update.sh ${D}${base_libdir}/rdk
    install -m 0755 ${WORKDIR}/chrony_tracking.sh ${D}${base_libdir}/rdk
    

    # service to start chrony
    rm -rf ${D}${systemd_unitdir}/system/chronyd.service
    install -m 0644 ${WORKDIR}/chronyd.service ${D}${systemd_unitdir}/system/
    install -m 0644 ${WORKDIR}/chrony-sync-notify.service ${D}${systemd_unitdir}/system/
    install -m 0644 ${WORKDIR}/chrony-tracking.service ${D}${systemd_unitdir}/system/
    install -m 0644 ${WORKDIR}/chrony-tracking.timer ${D}${systemd_unitdir}/system/

}


FILES:${PN} += "${sbindir}/chronyc"
FILES:${PN} += "${base_libdir}/rdk/chrony-sync-notify.sh"
CONFFILES:${PN} += "${sysconfdir}/chrony.conf"
CONFFILES:${PN} += "${sysconfdir}/rdk_chrony.conf"
FILES:${PN} += "${base_libdir}/rdk/chrony-conf-update.sh"
FILES:${PN} += "${base_libdir}/rdk/chrony_tracking.sh"
FILES_${PN} += "${systemd_unitdir}/system/chrony-tracking.service"
FILES_${PN} += "${systemd_unitdir}/system/chrony-tracking.timer"

SYSTEMD_SERVICE:${PN} += "chronyd.service"
SYSTEMD_SERVICE:${PN} += "chrony-sync-notify.service"
SYSTEMD_SERVICE:${PN} += "chrony-tracking.timer"


inherit syslog-ng-config-gen
SYSLOG-NG_FILTER = "chronyd"
SYSLOG-NG_SERVICE_chronyd = "chronyd.service"
SYSLOG-NG_DESTINATION_chronyd = "chrony.log"
SYSLOG-NG_LOGRATE_chronyd = "low"

