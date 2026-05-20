FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"

SRC_URI:append = " file://monit.service file://monit.path"

# Enable only the path unit so monit starts when the trigger file appears.
SYSTEMD_SERVICE:${PN} = "monit.path"

do_install:append() {
    # Replace the service installed by the base recipe with the layer-local one.
    rm -f ${D}${systemd_system_unitdir}/monit.service
    install -Dm 0644 ${UNPACKDIR}/monit.service ${D}${systemd_system_unitdir}/monit.service
    install -Dm 0644 ${UNPACKDIR}/monit.path ${D}${systemd_system_unitdir}/monit.path

    # Ensure monitrc from meta-rdk is installed.
    install -Dm 0600 ${UNPACKDIR}/monitrc ${D}${sysconfdir}/monitrc

    # Keep @prefix@ handling aligned with upstream recipe behavior.
    sed -i -e 's,@prefix@,${exec_prefix},g' ${D}${systemd_system_unitdir}/monit.service
}

FILES:${PN} += " \
    ${systemd_system_unitdir}/monit.path \
"
