SUMMARY = "Custom package group for CCSP bits used in RDK-B"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

PACKAGE_ARCH = "${TUNE_PKGARCH}"

inherit packagegroup

PACKAGES = "\
    packagegroup-rdk-ccsp-broadband \
"

# CCSP components used in RDK-B
RDEPENDS:packagegroup-rdk-ccsp-broadband = "\
    rbus \ 
    ccsp-common-library \
    ccsp-common-startup \
    utopia \
    ${CM_AGENT} \
    ccsp-cr \
    ccsp-cr-ccsp \
    ccsp-dmcli \
    ccsp-dmcli-ccsp \
    ccsp-home-security \
    ccsp-lm-lite \
    ccsp-lm-lite-ccsp \
    ccsp-adv-security \
    ccsp-xdns \
    ccsp-misc \
    ccsp-misc-ccsp \
    ccsp-eth-agent \
    ccsp-mta-agent \
    ccsp-mta-agent-ccsp \
    ccsp-p-and-m \
    ccsp-moca \
    ccsp-moca-ccsp \
    ccsp-p-and-m-ccsp \
    ccsp-psm \
    ccsp-psm-ccsp \
    ccsp-tr069-pa \
    ccsp-tr069-pa-ccsp \
    sysint-broadband \
    rfc \
    test-and-diagnostic \
    test-and-diagnostic-ccsp \
    ${WIFI_AGENT} \
    ccsp-hotspot \
    ccsp-hotspot-kmod \
    ${@bb.utils.contains('DISTRO_FEATURES', 'bci', bb.utils.contains('DISTRO_FEATURES', 'bci_webui_jst', 'jst','', d), bb.utils.contains('DISTRO_FEATURES', 'webui_jst', 'jst','', d), d)} \    
    ${@bb.utils.contains("DISTRO_FEATURES", "webui_php", "ccsp-webui-csrf", "", d)} \
    ${@bb.utils.contains("DISTRO_FEATURES", "bci", "ccsp-webui-csrf", "", d)} \
    ${@bb.utils.contains("DISTRO_FEATURES", "bridgeUtilsBin", "hal-bridgeutil", "", d)} \
    ccsp-xconf \
    hal-cm \
    hal-dhcpv4c \
    hal-ethsw \
    hal-moca \
    hal-mso_mgmt \
    hal-mta \
    hal-platform \
    hal-vlan \
    hal-wifi \
    breakpad-wrapper \
    xupnp \
    parodus \
    parodus2ccsp \
    dca \
    telemetry \
    ${@bb.utils.contains("DISTRO_FEATURES", "rfc-agent", "rfc-agent", "", d)} \
    webconfig-framework \
	${@bb.utils.contains("DISTRO_FEATURES", "enable_rdkscheduler", "rdk-scheduler", "", d)} \
    ${@bb.utils.contains("DISTRO_FEATURES", "wifimotion", "wifimotion", "", d)} \
    \
"
RDEPENDS:packagegroup-rdk-ccsp-broadband += "\
    ${@bb.utils.contains("DISTRO_FEATURES", "mlt", "sys-resource", " ", d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'bci', bb.utils.contains('DISTRO_FEATURES', 'bci_webui_jst', 'ccsp-webui-bci-jst','ccsp-webui-bci-php', d), bb.utils.contains('DISTRO_FEATURES', 'webui_jst', 'ccsp-webui-jst','ccsp-webui-php', d), d)} \
"
RDEPENDS:packagegroup-rdk-ccsp-broadband += " ${@bb.utils.contains('DISTRO_FEATURES', 'mqttCM', 'mqttcm', '', d)}"

RDEPENDS:packagegroup-rdk-ccsp-broadband += " ${@bb.utils.contains('DISTRO_FEATURES', 'aker', 'aker', '', d)}"

RDEPENDS:packagegroup-rdk-ccsp-broadband += " ${@bb.utils.contains('DISTRO_FEATURES', 'webconfig_bin', 'webcfg', '', d)}"

RDEPENDS:packagegroup-rdk-ccsp-broadband += " ${@bb.utils.contains('DISTRO_FEATURES', 'shorts', 'socat stunnel', '' , d)}"
RDEPENDS:packagegroup-rdk-ccsp-broadband += " ${@bb.utils.contains('DISTRO_FEATURES', 'snmppa', 'ccsp-snmp-pa ccsp-snmp-pa-ccsp', '', d)}"
RDEPENDS:packagegroup-rdk-ccsp-broadband += " ${@bb.utils.contains('DISTRO_FEATURES', 'usppa', "usp-pa", "", d)}"

RDEPENDS:packagegroup-rdk-ccsp-broadband += " ${@bb.utils.contains("DISTRO_FEATURES", "heaptrack", "heaptrack", "", d)}"
RDEPENDS:packagegroup-rdk-ccsp-broadband += " ${@bb.utils.contains("DISTRO_FEATURES", "gtestapp", "gtest-apps", "", d)}"

RDEPENDS:packagegroup-rdk-ccsp-broadband += " ${@bb.utils.contains("DISTRO_FEATURES", "rdkb_cellular_manager", "rdk-cellularmanager", "", d)}"
RDEPENDS:packagegroup-rdk-ccsp-broadband += " ${@bb.utils.contains("DISTRO_FEATURES", "rdkb_inter_device_manager", "rdk-interdevicemanager", "", d)}"

RDEPENDS:packagegroup-rdk-ccsp-broadband += " ${@bb.utils.contains('DISTRO_FEATURES', 'gateway_manager', ' gatewaymanager ', '', d)}"

RDEPENDS:packagegroup-rdk-ccsp-broadband += " ${@bb.utils.contains('DISTRO_FEATURES', 'pm_lan_mgr', ' lanmanager ', ' ${GWPROVAPP} ', d)}"
RDEPENDS:packagegroup-rdk-ccsp-broadband += " ${@bb.utils.contains('DISTRO_FEATURES', 'pm_lan_mgr', ' platformmanager ', ' ${GWPROVAPP} ', d)}"

DEPENDS += " ccsp-common-library"

WIFI_AGENT ?= "${@bb.utils.contains("DISTRO_FEATURES", "OneWifi", "ccsp-one-wifi", "ccsp-wifi-agent", d)}"
GWPROVAPP ?= "ccsp-gwprovapp ccsp-gwprovapp-ccsp"
CM_AGENT ?= "ccsp-cm-agent ccsp-cm-agent-ccsp"
