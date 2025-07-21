SUMMARY = "Memory insight utility and runner service"
SECTION = "console/utils"
DESCRIPTION = "meminsight: system/process memory statistics collection tool with systemd runner service."

LICENSE = "Apache-2.0"
#LIC_FILES_CHKSUM =

SRC_URI = "${CMF_GITHUB_ROOT}/meminsight;${CMF_GITHUB_SRC_URI_SUFFIX};name=meminsight"
SRCREV = "${AUTOREV}"

# Add the service file from your layer's files/ directory
SRC_URI:append = " file://meminsight-runner.service "

PACKAGE_ARCH = "${MACHINE_ARCH}"
SRCREV_FORMAT = "meminsight"
PV = "1.0"

S = "${WORKDIR}/git"

inherit systemd

SYSTEMD_SERVICE:${PN} = "meminsight-runner.service"

do_compile() {
    oe_runmake
}

do_install() {
    # Install the binary
    install -d ${D}${bindir}
    install -m 0755 ${S}/xMemInsight ${D}${bindir}/xMemInsight

    # Install the systemd service
    install -d ${D}${systemd_unitdir}/system
    install -m 0644 ${WORKDIR}/meminsight-runner.service ${D}${systemd_unitdir}/system/
}

FILES:${PN} += "${bindir}/xMemInsight"
FILES:${PN} += "${systemd_unitdir}/system/meminsight-runner.service"

