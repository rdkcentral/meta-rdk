FILESEXTRAPATHS:prepend := "${THISDIR}/files:"


SRC_URI += "file://chrony.conf \
            file://chronyd.service \
            file://rdk_chrony.conf \
           "

do_install:append() {
    # Binaries
    install -m 0755 ${S}/chronyc ${D}${sbindir}

    #config File
    rm ${D}${sysconfdir}/chrony.conf 
    install -m 755 ${WORKDIR}/chrony.conf ${D}${sysconfdir}/
    install -m 755 ${WORKDIR}/rdk_chrony.conf ${D}${sysconfdir}/

    # service to start chrony
    rm ${D}${systemd_unitdir}/system/chronyd.service
    install -m 0644 ${WORKDIR}/chronyd.service ${D}${systemd_unitdir}/system/

}

FILES:${PN} += "${sbindir}/chronyc"
CONFFILES:${PN} += "${sysconfdir}/default/chrony.conf"
CONFFILES:${PN} += "${sysconfdir}/rdk_chrony.conf"

SYSTEMD_SERVICE:${PN} += "chronyd.service"


inherit syslog-ng-config-gen
SYSLOG-NG_FILTER = "chronyd"
SYSLOG-NG_SERVICE_chronyd = "chronyd.service"
SYSLOG-NG_DESTINATION_chronyd = "chrony.log"
SYSLOG-NG_LOGRATE_chronyd = "low"

# Separate the client program into its own package
#FILES:${PN} += "${sbindir}/chrony_ctl_daemon"
