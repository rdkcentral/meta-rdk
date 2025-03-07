
OPTIMIZE_DEFAULT = "-O2"

LAYERDEPENDS_rdk:remove = "multimedia-layer"

#set platform type (broadband, hybrid, or client)
MACHINEOVERRIDES .= ":rdkx:voicecontrol"

PRODUCT_apq8009         :="base"
PRODUCT_SUBTYPE_apq8009 :=""

# Allow missing base recipe files for our bbappends
BB_DANGLINGAPPENDS_WARNONLY ?= "true"

PREFERRED_VERSION_openssl = "1.0.2o"

include conf/distro/include/rdkx-cpc.inc
