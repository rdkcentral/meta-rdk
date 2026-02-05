PARALLEL_MAKE:append = " -l ${@int(os.sysconf(os.sysconf_names['SC_NPROCESSORS_ONLN'])) * 150/100}"

# RDK-E updating the version to RDK-42422 to use from OE
PREFERRED_VERSION_jansson = "2.13.1"

PREFERRED_VERSION_icu ?= "64.1"
PREFERRED_VERSION_icu-native ?= "64.1"
PREFERRED_VERSION_icu_dunfell ?= "66.1"
PREFERRED_VERSION_icu-native_dunfell ?= "66.1"
PREFERRED_VERSION_icu:kirkstone ?= "70.1"
PREFERRED_VERSION_icu-native:kirkstone ?= "70.1"

PREFERRED_VERSION_curl ?= "7.60.0"
PREFERRED_VERSION_curl-native ?= "7.60.0"
PREFERRED_VERSION_curl_dunfell ?= "7.69.1"
PREFERRED_VERSION_curl-native_dunfell ?= "7.69.1"

PREFERRED_VERSION_openssl = "1.0.2o"
PREFERRED_VERSION_openssl_dunfell = "1.1.1d"

PREFERRED_VERSION_brotli_dunfell = "1.0.7"
PREFERRED_VERSION_brotli-native_dunfell = "1.0.7"

PREFERRED_VERSION_brotli:kirkstone = "1.0.9"
PREFERRED_VERSION_brotli-native:kirkstone = "1.0.9"

PREFERRED_VERSION_libwebsockets:kirkstone = "4.2.2"

# Disabling Dunfell version of breakpad for timebeing due to DELIA-48240. Till issue is fixed morty version of breakpad will be used in dunfell builds.
PREFERRED_VERSION_breakpad_dunfell = "${@bb.utils.contains('DISTRO_FEATURES', 'breakpadgit', 'git', 'svn', d)}"
PREFERRED_VERSION_breakpad-native_dunfell = "${@bb.utils.contains('DISTRO_FEATURES', 'breakpadgit', 'git', 'svn', d)}"

BBMASK .= "|.*recipes-ccsp|.*recipes-rdkb|.*webpabroadband"

# Build flags to control default SecAPI engine for netflix component
DISTRO_FEATURES:append = " netflix_cryptanium"

BBMASK .= "|meta-rdk-comcast-video/recipes-extended/mediaframework/emu-podserver_git.bb"
BBMASK .= "|meta-rdk-comcast-video/recipes-extended/halsnmp/halsnmp-emu_git.bb"
BBMASK .= "|.meta-rdk-comcast/recipes-common/nvm-sqlite/nvm-sqlite.bb"
BBMASK .= "|meta-rdk-comcast/recipes-common/rdm/rdm.bbappend"


# add rdkfwupgrader.bb to bbmask till such time that it supports video devices.
# BBMASK .= "|.meta-rdk-comcast/recipes-extended/rdkfwupgrader/rdkfwupgrader.bb"

# add webconfig-framework.bb to bbmask till such time that it supports Video devices.
# BBMASK .= "|.meta-rdk/recipes-common/webconfig-framework/webconfig-framework.bb"

DISTRO_FEATURES:append = " playreadycdmi_cryptanium"
DISTRO_FEATURES:append = " widevine_hardware"

DISTRO_FEATURES:append = " cgroup"

DISTRO_FEATURES:remove = "nfs"

DISTRO_FEATURES:append = " xcal_device"

DISTRO_FEATURES:append = " lxy"

DISTRO_FEATURES:append  = " bind"
DISTRO_FEATURES:append = " safec"

DISTRO_FEATURES:append = " webconfig_bin"


PREFERRED_VERSION_bluez5_dunfell ?= "5.54"
PREFERRED_VERSION_bluez5:kirkstone ?= "5.48"
DISTRO_FEATURES:append = " logendpoints"
DISTRO_FEATURES:remove_morty = " logendpoints"

DISTRO_FEATURES:append = " tigress"


OPTIMIZE_DEFAULT = "-Os"

PREFERRED_VERSION_lighttpd = "1.4.53"

PREFERRED_VERSION_cairo_daisy ?= "1.12.16"
PREFERRED_VERSION_cairo_dunfell ?= "1.16.0"
PREFERRED_VERSION_cairo:kirkstone ?= "1.16.0"
#Downgrade the cairo version since 1.16.0 is having compatibility issues in Arm devices.
PREFERRED_VERSION_cairo_dunfell:arm = "1.14.6"

PREFERRED_VERSION_systemd ?= "1:230%"

PREFERRED_VERSION_mosquitto_dunfell = "1.6.%"
PREFERRED_VERSION_mosquitto:kirkstone = "2.0.14"

PREFERRED_VERSION_nodejs:kirkstone = "16.14.2"
PREFERRED_VERSION_nodejs-native:kirkstone = "16.14.2"

PREFERRED_VERSION_qtwebsockets = "${@bb.utils.contains('DISTRO_FEATURES', 'comcast_qt5', '5.1.1%', '5.15.7', d)}"


DISTRO_FEATURES:append  = " rdkssa"
#RDKPI-429
DISTRO_FEATURES:remove_rpi = "rdkssa"

#State Red for RDK-V
DISTRO_FEATURES:append  = " redrecovery"
DISTRO_FEATURES:remove_rpi = " redrecovery"

DISTRO_FEATURES_NATIVE:append = " bpd_symbol_compression"
DISTRO_FEATURES_NATIVE:remove_daisy = " bpd_symbol_compression"

BBMASK .= "${@bb.utils.contains('DISTRO_FEATURES', 'ssl-1.1.1', '|.meta-rdk-ext/recipes-support/stunnel/stunnel_5.56.bbappend|.meta-rdk-video/recipes-support/curl/curl_7.69%.bbappend', '', d)}"
BBMASK .= "${@bb.utils.contains('DISTRO_FEATURES', 'ssl-1.1.1', '|.meta-rdk-ext/recipes-connectivity/openssl/openssl-1.0.2o_1.0.2o.bb', '', d)}"

inherit ${@bb.utils.contains('DISTRO_FEATURES', 'sbom', 'create-spdx', '', d)}

PREFERRED_VERSION_libcobalt_morty = "21.lts.stable"


# Guarding RDK-40652 with DISTRO
DISTRO_FEATURES:append:dunfell = " systemd-journal-cache"

#Masking the below recipes because in kirkstone build recipes pulling older version due to layer priority
BBMASK .= "${@bb.utils.contains('DISTRO_FEATURES', 'kirkstone', '|.meta-rdk-ext/recipes-support/fcgi/fcgi_2.4.0.bb', '', d)}"
BBMASK .= "${@bb.utils.contains('DISTRO_FEATURES', 'kirkstone', '|.meta-rdk-ext/recipes-devtools/jsonrpc/jsonrpc_0.7.0.bb', '', d)}"
BBMASK .= "${@bb.utils.contains('DISTRO_FEATURES', 'kirkstone', '|.meta-rdk-ext/recipes-devtools/jsoncpp/jsoncpp_0.6.0-rc2.bb', '', d)}"
BBMASK .= "${@bb.utils.contains('DISTRO_FEATURES', 'kirkstone', '|.meta-rdk-ext/recipes-common/spawn-fcgi/spawn-fcgi_1.6.3.bb', '', d)}"
BBMASK .= "${@bb.utils.contains('DISTRO_FEATURES', 'kirkstone', '|.meta-rdk-ext/recipes-support/libev/libev_4.24.bb', '', d)}"
BBMASK .= "${@bb.utils.contains('DISTRO_FEATURES', 'kirkstone', '|.meta-rdk-ext/recipes-support/libtinyxml2/libtinyxml2_git.bb', '', d)}"
BBMASK .= "${@bb.utils.contains('DISTRO_FEATURES', 'kirkstone', '|.meta-rdk-ext/recipes-extended/procps/procps_3.3.12.bb', '', d)}"
BBMASK .= "${@bb.utils.contains('DISTRO_FEATURES', 'kirkstone', '|.meta-rdk-ext/recipes-devtools/doxygen/doxygen_1.7.6.1.bb', '', d)}"

DISTRO_FEATURES:append ?= " wpe_r4_4"
PREFERRED_VERSION_wpeframework = "${@bb.utils.contains('DISTRO_FEATURES', 'wpe_r4_4', '4.4%' , bb.utils.contains('DISTRO_FEATURES', 'wpe_r4', '4.2%', '2.1%', d), d)}"
PREFERRED_VERSION_wpeframework-clientlibraries = "${@bb.utils.contains('DISTRO_FEATURES', 'wpe_r4_4', '4.4%' , bb.utils.contains('DISTRO_FEATURES', 'wpe_r4', '4.2%', '2.1%', d), d)}"
PREFERRED_VERSION_entservices-apis = "1.0.1"
PREFERRED_VERSION_wpeframework-tools-native = "${@bb.utils.contains('DISTRO_FEATURES', 'wpe_r4_4', '4.4%' , bb.utils.contains('DISTRO_FEATURES', 'wpe_r4', '4.2%', '2.1%', d), d)}"

# MaintenanceManager Journal Logging
DISTRO_FEATURES:append = " maintenance_journal_logging"

include conf/distro/include/rdkv-cpc.inc

# Distro feature for NetworkManager
DISTRO_FEATURES:append = " ENABLE_NETWORKMANAGER"

#Distro feature for WPEFramework SecurityUtility Disable
DISTRO_FEATURES:append = " wpe_security_util_disable"

#RDK-SSA Generic daemon and ca provider with PKCS11 support
DISTRO_FEATURES:remove = "ssacertifier"
DISTRO_FEATURES:append = " rdkvgenericssa"
