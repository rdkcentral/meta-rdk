FILESEXTRAPATHS:prepend := "${THISDIR}/files:"


SRC_URI += "file://chrony.conf \
            file://chrony_Envfile \
           "
         

do_install:append() {
    # Binaries
    install -m 0755 ${S}/chronyc ${D}${sbindir}
    install -d ${D}${sysconfdir}/default
    install -m 0755 ${WORKDIR}/chrony_Envfile ${D}${sysconfdir}/default/chronyd  #(TBD- review permissions)

    #config File
    install -m 755 ${WORKDIR}/chrony.conf ${D}${sysconfdir}/default/chrony.conf

    # service to start chrony
    rm ${D}${systemd_unitdir}/system/chronyd.service
    install -m 0644 ${WORKDIR}/chronyd.service ${D}${systemd_unitdir}/system/

}

FILES:${PN} += "${sbindir}/chronyc"
CONFFILES:${PN} = "${sysconfdir}/default/chrony.conf"

SYSTEMD_SERVICE:${PN} += "chronyd.service"


inherit syslog-ng-config-gen
SYSLOG-NG_FILTER = "chronyd"
SYSLOG-NG_SERVICE_chronyd = "chronyd.service"
SYSLOG-NG_DESTINATION_chronyd = "chrony.log"
SYSLOG-NG_LOGRATE_chronyd = "low"

# Separate the client program into its own package
#FILES:${PN} += "${sbindir}/chrony_ctl_daemon"
