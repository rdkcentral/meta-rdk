FILESEXTRAPATHS:prepend := "${THISDIR}/files:"


SRC_URI += "file://chrony.conf \
            file://chronyd.service \
            file://rdk_chrony.conf \
            file://chrony-sync-notify.sh \
            file://chrony-sync-notify.service \
           "

do_install:append() {
    # Binaries
    install -m 0755 ${S}/chronyc ${D}${sbindir}
    install -d ${D}${base_libdir}/rdk

    #config File
    rm ${D}${sysconfdir}/chrony.conf 
    install -m 755 ${WORKDIR}/chrony.conf ${D}${sysconfdir}/
    install -m 755 ${WORKDIR}/rdk_chrony.conf ${D}${sysconfdir}/
    install -m 755 ${WORKDIR}/chrony-sync-notify.sh ${D}${base_libdir}/rdk
    

    # service to start chrony
    rm ${D}${systemd_unitdir}/system/chronyd.service
    install -m 0644 ${WORKDIR}/chronyd.service ${D}${systemd_unitdir}/system/
    install -m 755 ${WORKDIR}/chrony-sync-notify.service ${D}${systemd_unitdir}/system/

}

FILES:${PN} += "${sbindir}/chronyc"
FILES:${PN} += "${base_libdir}/rdk/chrony-sync-notify.sh"
CONFFILES:${PN} += "${sysconfdir}/chrony.conf"
CONFFILES:${PN} += "${sysconfdir}/rdk_chrony.conf"

SYSTEMD_SERVICE:${PN} += "chronyd.service"
SYSTEMD_SERVICE:${PN} += "chrony-sync-notify.service"


inherit syslog-ng-config-gen
SYSLOG-NG_FILTER = "chronyd"
SYSLOG-NG_SERVICE_chronyd = "chronyd.service"
SYSLOG-NG_DESTINATION_chronyd = "chrony.log"
SYSLOG-NG_LOGRATE_chronyd = "low"

# Separate the client program into its own package
#FILES:${PN} += "${sbindir}/chrony_ctl_daemon"
