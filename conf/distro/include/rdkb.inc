# Headless disable graphical splash
SPLASH = ""

DISTRO_FEATURES:append:dunfell = " virtualization"
PREFERRED_VERSION_systemd = "1:250%"

PREFERRED_VERSION_lighttpd = "1.4.53"
PREFERRED_VERSION_libupnp = "1.6.22"
PREFERRED_VERSION_jansson = "2.7"
PREFERRED_VERSION_libnetfilter-conntrack_daisy = "1.0.2"

# Preferred gtest version/provider
PREFERRED_VERSION_googletest = "1.10.0"
PREFERRED_PROVIDER_gtest = "googletest"
PREFERRED_PROVIDER_gmock = "googletest"

DISTRO_FEATURES:append = " bluetooth"
DISTRO_FEATURES:append = " bluez5"
PREFERRED_VERSION_bluez5_dunfell ?= "5.66"
PREFERRED_VERSION_bluez5:kirkstone ?= "5.66"

PREFERRED_VERSION_php ?= "7.2.%"
PREFERRED_VERSION_php-native ?= "7.2.%"
PREFERRED_VERSION_php_dunfell ?= "7.4.%"
PREFERRED_VERSION_php-native_dunfell ?= "7.4.%"

PREFERRED_VERSION_log4c = "1.2.3"
PREFERRED_VERSION_net-snmp = "5.8"
PREFERRED_VERSION_avro-c = "1.8.1"
PREFERRED_VERSION_avro-c:kirkstone = "1.11.0"

#PREFERRED_VERSION_icu ?= "64.2"
#PREFERRED_VERSION_icu-native ?= "64.2"
PREFERRED_VERSION_icu ?= "64.%"
PREFERRED_VERSION_icu-native ?= "64.%"
PREFERRED_VERSION_icu_dunfell ?= "66.1"
PREFERRED_VERSION_icu-native_dunfell ?= "66.1"

PREFERRED_VERSION_gupnp ?= "1.2.%"
PREFERRED_VERSION_openssl_dunfell = "1.0.2o"
PREFERRED_VERSION_openssl_morty = "1.0.2o"

# Adding PREFERRED_VERSION for smcroute
PREFERRED_VERSION_smcroute ?= "2.0.0"

PREFERRED_PROVIDER_hal-cm ?= "hal-cm-generic"
PREFERRED_PROVIDER_hal-dhcpv4c ?= "hal-dhcpv4c-generic"
PREFERRED_PROVIDER_hal-ethsw ?= "hal-ethsw-generic"
PREFERRED_PROVIDER_hal-moca ?= "hal-moca-generic"
PREFERRED_PROVIDER_hal-mso_mgmt ?= "hal-mso-mgmt-generic"
PREFERRED_PROVIDER_hal-mta ?= "hal-mta-generic"
PREFERRED_PROVIDER_hal-platform ?= "hal-platform-generic"
PREFERRED_PROVIDER_hal-vlan ?= "hal-vlan-generic"
PREFERRED_PROVIDER_hal-wifi ?= "hal-wifi-generic"
PREFERRED_PROVIDER_hal-epon ?= "hal-epon-generic"
PREFERRED_PROVIDER_hal-firewall ?= "hal-firewall-generic"

PREFERRED_VERSION_libmtp = "1.1.5"
PREFERRED_VERSION_mosquitto_dunfell = "1.6.%"
PREFERRED_VERSION_mosquitto:kirkstone = "2.0.14"
PREFERRED_VERSION_gnupg_dunfell = "1.4.7"
DISTRO_FEATURES:append:dunfell = " logendpoints"

DISTRO_FEATURES:append = " tigress"
include conf/distro/include/rdkb-cpc.inc

PREFERRED_VERSION_cjson:kirkstone = "1.7.15"
PREFERRED_PROVIDER_xfhome ?= "zilker"

PREFERRED_VERSION_iksemel ?= '${@oe.utils.conditional("PREFERRED_PROVIDER_xfhome", "touchstone", "1.4%", "", d)}'

VOLATILE_BINDS:append = "/usr/ccsp/ccsp_msg.cfg /ccsp_msg.cfg\n"

BBMASK .= "|.meta-rdk/recipes-core/images/rdk-oss-image.bb|meta-rdk/recipes-core/images/rdk-generic-image.bb"
BBMASK .= "|.*recipes-extended/mediaframework|.*recipes-qt"
BBMASK .= "|.meta-rdk-comcast/recipes-support/crypt-utils/*|.meta-rdk-comcast/recipes-extended/whitebox|.meta-rdk/recipes-oem/mfrlib"
BBMASK .= "|.meta-rdk-comcast/recipes-common/sec-pks-client/sec-pks-client.bb"
BBMASK .= "|.meta-rdk-comcast/recipes-common/tdk-cpc"
BBMASK .= "|.meta-rdk/recipes-extended/wifi-hal-headers|.meta-rdk-comcast/recipes-extended/acscerts"
BBMASK .= "|.meta-rdk/recipes-common/nlmonitor"
BBMASK .= "|.meta-rdk-comcast/recipes-common/cgrouputils"
BBMASK .= "|.meta-rdk-ext/recipes-extended|.meta-rdk-ext/recipes-multimedia|.meta-rdk-ext/recipes-graphics|.meta-rdk-ext/recipes-utils"
BBMASK .= "|.meta-rdk-comcast/recipes-common/nvm-sqlite/nvm-sqlite.bb|.meta-rdk-comcast/recipes-common/rdkversion/rdkversion_1.0.bb"
BBMASK .= "|.meta-rdk-soc-intel-gw/meta-intelce-arm/recipes-gateway/miniupnpd/miniupnpd.bb"
BBMASK .= "|.meta-rdk/recipes-core/packagegroups/packagegroup-rdk-qt5.bb"
BBMASK .= "|.meta-rdk-comcast/recipes-core/packagegroups/packagegroup-rdk-qt5.bb"
VOIP_MASK ?= "|.meta-rdk-broadband/recipes-ccsp/ccsp/ccsp-telcovoip-agent.bb"
BBMASK .= "${VOIP_MASK}"
WAN_MASK ?= "|.meta-rdk-broadband/recipes-ccsp/ccsp/ccsp-wanagent.bb"
BBMASK .= "${WAN_MASK}"
DSL_MASK ?= "|.meta-rdk-broadband/recipes-ccsp/ccsp/ccsp-dslagent.bb|.meta-rdk-broadband/recipes-ccsp/ccsp/ccsp-xtmagent.bb"
BBMASK .= "${DSL_MASK}"
VLAN_MASK ?= "|.meta-rdk-broadband/recipes-ccsp/ccsp/ccsp-vlanagent.bb"
BBMASK .= "${VLAN_MASK}"

FEATURE_WANMANAGER_DSL_MASK ?= "|.meta-rdk-broadband/recipes-ccsp/ccsp/rdkxdslmanager.bb"
BBMASK .= "${FEATURE_WANMANAGER_DSL_MASK}"
FEATURE_WANMANAGER_WAN_MASK ?= "|.meta-rdk-broadband/recipes-ccsp/ccsp/rdk-wanmanager.bb"
BBMASK .= "${FEATURE_WANMANAGER_WAN_MASK}"
FEATURE_WANMANAGER_VLAN_MASK ?= "|.meta-rdk-broadband/recipes-ccsp/ccsp/rdk-vlanmanager.bb"
BBMASK .= "${FEATURE_WANMANAGER_VLAN_MASK}"
FEATURE_WANMANAGER_GPON_MASK ?= "|.meta-rdk-broadband/recipes-ccsp/ccsp/rdkgponmanager.bb"
BBMASK .= "${FEATURE_WANMANAGER_GPON_MASK}"
FEATURE_WANMANAGER_PPP_MASK ?= "|.meta-rdk-broadband/recipes-ccsp/ccsp/rdk-ppp-manager.bb"
BBMASK .= "${FEATURE_WANMANAGER_PPP_MASK}"
FEATURE_WANMANAGER_FWUPGRADE_MASK ?= "|.meta-rdk-broadband/recipes-ccsp/ccsp/rdk-fwupgrade-manager.bb"
BBMASK .= "${FEATURE_WANMANAGER_FWUPGRADE_MASK}"
FEATURE_WANMANAGER_VOICEMGR_MASK ?= "|.meta-rdk-broadband/recipes-ccsp/ccsp/rdktelcovoicemanager.bb"
BBMASK .= "${FEATURE_WANMANAGER_VOICEMGR_MASK}"
FEATURE_WANMANAGER_IPOE_MASK ?= "|.meta-rdk-broadband/recipes-support/ipoe-health-check/ipoe-health-check.bb"
BBMASK .= "${FEATURE_WANMANAGER_IPOE_MASK}"
FEATURE_WANMANAGER_JSONHAL_MASK ?= "|.meta-rdk-broadband/recipes-support/json-hal/json-hal-lib.bb"
BBMASK .= "${FEATURE_WANMANAGER_JSONHAL_MASK}"
FEATURE_RDKB_LED_MANAGER ?= "|.meta-rdk-broadband/recipes-ccsp/ccsp/rdk-ledmanager.bb"
BBMASK .= "${FEATURE_RDKB_LED_MANAGER}"
FEATURE_CELLULAR_MANAGER_MASK ?= "|.meta-rdk-broadband/recipes-ccsp/ccsp/rdk-cellularmanager.bb"
BBMASK .= "${FEATURE_CELLULAR_MANAGER_MASK}"
FEATURE_RDKB_INTER_DEVICE_MANAGER ?= "|.meta-rdk-broadband/recipes-ccsp/ccsp/rdk-interdevicemanager.bb"
BBMASK .= "${FEATURE_RDKB_INTER_DEVICE_MANAGER}"
FEATURE_RDKB_LAN_MANAGER ?= "|.meta-rdk-broadband/recipes-ccsp/ccsp/lanmanager.bb"
BBMASK .= "${FEATURE_RDKB_LAN_MANAGER}"
FEATURE_RDKB_PLATFORM_MANAGER ?= "|.meta-rdk-broadband/recipes-ccsp/ccsp/platformmanager.bb"
BBMASK .= "${FEATURE_RDKB_PLATFORM_MANAGER}"

HUBLINUX_MASK ?= "|.meta-rdk-soc-broadcom-broadband/meta-brcm-broadband/recipes-kernel/linux/bcalinux_4.19.bb"
BBMASK .= "${HUBLINUX_MASK}"

# add rdkversion_1.0.bb is not supported for broadband devices.
BBMASK .= "|./meta-rdk/recipes-common/rdkversion/"

DISTRO_FEATURES:append = " telemetry2_0"
DISTRO_FEATURES:append = " snmppa"

# add new breakpad framework for RDKB
DISTRO_FEATURES:append  = " logbacktrace"
require conf/distro/include/rdkb_backtrace_log.inc

DISTRO_FEATURES:append = " heaptrack"
DISTRO_FEATURES:remove_morty = " heaptrack"
PREFERRED_VERSION_heaptrack:kirkstone = "1.2.%"

DISTRO_FEATURES:remove = "alsa"
DISTRO_FEATURES:remove = "ext2"
DISTRO_FEATURES:remove = "zeroconf"
DISTRO_FEATURES:remove = "nfs"
DISTRO_FEATURES:append = " safec"
#DISTRO_FEATURES:append = " webui_php"
DISTRO_FEATURES:append = " webui_jst"
#DISTRO_FEATURES:append = " gtestapp"
DISTRO_FEATURES:append = " redrecovery"
OPTIMIZE_DEFAULT = "-Os"



include conf/include/oss-config.inc
PREFERRED_VERSION_gettext-native = "0.21"
PREFERRED_PROVIDER_ca-certificates-trust-store = "ca-certificates-trust-store"

BBMASK .= "|meta-rdk-comcast/recipes-core/systemd/systemd_250.5.bbappend"
BBMASK .= "|meta-rdk-comcast-broadband/recipes-core/systemd/systemd_%.bbappend"
BBMASK .= "|meta-rdk/recipes-common/safec/safec_%.bbappend"
BBMASK .= "|meta-rdk-comcast/recipes-connectivity/ndisc6/ndisc6_%.bbappend"
BBMASK .= "|meta-rdk-comcast/recipes-support/ca-certificates/ca-certificates_%.bbappend"
BBMASK .= "|meta-rdk-comcast/recipes-connectivity/dibbler/dibbler_git.bbappend"
BBMASK .= "|meta-rdk-comcast/recipes-common/lighttpd/lighttpd_%.bbappend"
BBMASK .= "|meta-rdk-comcast-broadband/recipes-common/bash/bash_%.bbappend"
BBMASK .= "|meta-rdk-comcast/recipes-devtools/breakpad/breakpad_git.bbappend"
BBMASK .= "|meta-rdk-comcast/recipes-tweaks/breakpad/breakpad_git.bbappend"
BBMASK .= "|meta-rdk-comcast/recipes-core/glib-2.0/glib-2.0_2.7%.bbappend"
BBMASK .= "|meta-rdk-broadband/recipes-support/libsoup/libsoup-2.4_2.74.2.bbappend"
BBMASK .= "|meta-rdk-comcast/recipes-support/stunnel/stunnel_%.bbappend"
BBMASK .= "|meta-rdk/recipes-extended/rpcbind/rpcbind_1.2.%.bbappend"
BBMASK .= "|meta-rdk-comcast/recipes-connectivity/bind/bind_%.bbappend"
BBMASK .= "|meta-rdk-comcast-broadband/recipes-rdkb/iptables/iptables_1.8.%.bbappend"
BBMASK .= "|meta-rdk-comcast-broadband/recipes-rdkb/iptables/iptables_%.bbappend"
BBMASK .= "|meta-rdk-comcast-broadband/recipes-support/syslog-ng/syslog-ng_3.36.1.bbappend"
BBMASK .= "|meta-rdk-comcast-broadband/recipes-connectivity/dibbler/dibbler_%.bbappend"
BBMASK .= "|meta-rdk-comcast-broadband/recipes-core/dropbear/dropbear_2020.81.bbappend"
BBMASK .= "|meta-rdk-comcast/recipes-protocols/net-snmp/*"
BBMASK .= "|meta-rdk/recipes-support/webcfg/webcfg_1.0.bb"
BBMASK .= "|meta-rdk-comcast-broadband/recipes-common/rfc/rfc_git.bbappend"
BBMASK .= "|meta-rdk-broadband/recipes-support/cimplog/cimplog_1.0.bbappend"

PREFERRED_VERSION_jsoncpp = "1:1.9.5"

DISTRO_FEATURES:append = " rdkb_configurable_wan_interface "
DISTRO_FEATURES:append = " unified_mapt "

BBMASK .= "|meta-rdk-oem-tch-brcm-xb6/meta-tchxb6/recipes-bsp/broadcom-thread/broadcom-thread.bb|.meta-rdk-oem-tch-brcm-xb6/meta-tchxb6/recipes-bsp/broadcom-thread/broadcom-thread_17.2.bb|.meta-rdk-oem-tch-brcm-xb6/meta-tchxb6/recipes-bsp/broadcom-thread/broadcom-thread_18.1.bb|.meta-rdk-oem-tch-brcm-xb6/meta-tchxb6/recipes-bsp/broadcom-thread/broadcom-thread_18.3.bb|.meta-rdk-oem-tch-brcm-xb6/meta-tchxb6/recipes-bsp/broadcom-thread/broadcom-thread_19.3.bb|.meta-rdk-oem-tch-brcm-xb6/meta-tchxb6/recipes-bsp/broadcom-thread/broadcom-thread-bin_17.2.bb|.meta-rdk-oem-tch-broadcom-wifi/meta-tchbcm/recipes-bsp/broadcom-wifi/broadcom-wifi_19.3.bb"
MIDDLEWARE_ARCH = "cortexa15hf-neon"
#This should be reviewed and added it back.
#OSS_LAYER_ARCH = "cortexa15hf-neon"
IMAGE_CLASSES:remove = " post-rootfs-hooks "


#srcrev for opensourced components


SRCREV:pn-libsyswrapper = "5143ba5b92f5dc77b436837d51f2f612d5846b7b"
SRCREV_rdk-libunpriv = "a0dbea7d368a630158abf56ef8bd7f3855910ec5"
SRCREV:pn-telemetry = "0d73470cc038f1e47ffee483a7d05e51bf88751e"
SRCREV:pn-crashupload = "8e7e22d2cb988ea58b9ba9d85b8b0812c6dc77d2"
SRCREV:pn-commonutilities = "071361f284ba9049bf7d8cb9a75b583b9b1e353b"
SRCREV:pn-rfc = "b6da366e704a006394b33759f144c0a4256fd335"
SRCREV:pn-rdkcertconfig = "e0e35743f7e96ad0595ac25b11d829b766f7062b"
SRCREV_caupdate = "9859720a8748331038e5476321cf039e7d17c5f1"
#topic branch - rdk-ca-store
SRCREV_rdk-ca-store = "378b7d149a0a0c71be7eb41c0ac0fc79a3ca1cf9"
SRCREV:pn-sslcerts = "2d3ef2f7c1b54c7552cac2ab1e41f1ce3871c3d6"
#topic branch - ssacpc
SRCREV_ssacpc = "f238816d57f9956d1ddcd6754b170eafec0ec47e"
#topic branch -cedmcpc
SRCREV_cedmcpc = "a82a07c5c2639a81d4bcd2e2d665ce06c999019c"
SRCREV_ecfsgeneric= "822d726d14e575ec9c73198ce939733abd0d365b"
SRCREV:pn-remotedebugger = "300d29e105f6a7244bff81fa0357dac914dd8913"
SRCREV_sshkeys = "3a1c7d6a07586117bd992fe72ae3365fdd7ef2f3"
SRCREV_mount-utils = "c1aeb29c8e1f236babdd6a1a4538c5b451c7b8c1"
