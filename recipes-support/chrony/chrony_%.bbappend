FILESEXTRAPATHS:prepend := "${THISDIR}/files:"


SRC_URI += "file://chronyd.service \
            file://time_driver.h \
            file://time_manager.c \
            file://driver_chrony_socket.c \
            file://candm.h"
            
do_compile:append() {
    ${CC} ${CFLAGS} ${LDFLAGS} -I${S} -D_GNU_SOURCE -c ${WORKDIR}/time_manager.c -o ${S}/time_manager.o
    ${CC} ${CFLAGS} ${LDFLAGS} -I${S} -D_GNU_SOURCE -c ${WORKDIR}/driver_chrony_socket.c -o ${S}/driver_chrony_socket.o
    ${CC} ${CFLAGS} ${LDFLAGS} time_manager.o driver_chrony_socket.o -lm -o ${S}/chrony_ctl_daemon
}            

do_install:append() {
    # Binaries
     #install -d ${D}${base_libdir}/rdk
    install -m 0755 ${S}/chronyc ${D}${sbindir}
    install -m 0755 ${S}/chrony_ctl_daemon ${D}${sbindir}/

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
FILES:${PN} += "${sbindir}/chrony_ctl_daemon"
