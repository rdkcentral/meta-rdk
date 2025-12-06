DESCRIPTION = "The client agent for the Xmidt service."
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${THISDIR}/files/Apache-2.0;md5=3b83ef96387f14655fc854ddc3c6bd57"

PV = "1.0.0"
PR = "r0"
PACKAGE_ARCH = "${MIDDLEWARE_ARCH}"

PKG_VERSION = "0.5.15"

SRC_URI = "https://github.com/xmidt-org/xmidt-agent/releases/download/v${PKG_VERSION}/xmidt-agent_${PKG_VERSION}_rdk_armv7.ipk;name=xmidt-agent"
SRC_URI[xmidt-agent.md5sum] = "a42eef93f5f129da6a25f1ce5386b2ee"
SRC_URI[xmidt-agent.sha256sum] = "c4fb451d4dfbf51ae71f35bb33dfc8c85ae9ff0a2139fc46e96cac859adbf62e"

S = "${WORKDIR}"

do_unpack() {
    cd ${DL_DIR}
    tar -xvf xmidt-agent_${PKG_VERSION}_rdk_armv7.ipk -C ${S}
}

do_install() {
    if [ -f ${S}/data.tar.gz ]; then
        tar -xzf data.tar.gz -C ${D}
    elif [ -f ${S}/data.tar.xz ]; then
        tar -xJf data.tar.xz -C ${D}
    else
        echo "No data archive found in the IPK"
        exit 1
    fi
}

FILES_${PN} += "${bindir}/xmidt-agent ${sysconfdir}/xmidt-agent/01-config.yml"
FILES:${PN} += "${systemd_unitdir}/system/xmidt-agent.service"
SYSTEMD_SERVICE_${PN} += "xmidt-agent.service"
INSANE_SKIP:${PN} += "already-stripped"
