FILESEXTRAPATHS:prepend := "${THISDIR}/files:"


SRC_URI += "file://chronyd.service"
            
            

do_install:append() {
    # Binaries
     #install -d ${D}${base_libdir}/rdk
    install -m 0755 ${S}/chronyc ${D}${sbindir}

    #config File
    install -m 644 ${WORKDIR}/chrony.conf ${D}${sysconfdir}

    # service to start chrony
    rm ${D}${systemd_unitdir}/system/chronyd.service
    install -m 0644 ${WORKDIR}/chronyd.service ${D}${systemd_unitdir}/system/

}

FILES:${PN} += "${sbindir}/chronyc"
CONFFILES:${PN} = "${sysconfdir}/chrony.conf"

SYSTEMD_SERVICE:${PN} += "chronyd.service"


inherit syslog-ng-config-gen
SYSLOG-NG_FILTER = "chronyd"
SYSLOG-NG_SERVICE_chronyd = "chronyd.service"
SYSLOG-NG_DESTINATION_chronyd = "chrony.log"
SYSLOG-NG_LOGRATE_chronyd = "low"

# Separate the client program into its own package
