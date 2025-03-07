# enable gstreamer1
DISTRO_FEATURES:append = " gstreamer1"

# toggle gstreamer 1.4 and 1.10

# Comment out Old version & use the latest version
#GST_VERSION = "1.4.4"
#ORC_VERSION = "0.4.18"

#GST_VERSION ?= "1.10.4"
#ORC_VERSION = "0.4.26"

GST_VERSION ?= " ${@bb.utils.contains('DISTRO_FEATURES', 'gstreamer1_18', '1.18.5', '1.10.4', d)}"
ORC_VERSION = " ${@bb.utils.contains('DISTRO_FEATURES', 'gstreamer1_18', '0.4.31', '0.4.26', d)}"

PREFERRED_VERSION_gstreamer1.0 ??= "${GST_VERSION}"
PREFERRED_VERSION_gstreamer1.0-libav ??= "${GST_VERSION}"
PREFERRED_VERSION_gstreamer1.0-plugins-bad ??= "${GST_VERSION}"
PREFERRED_VERSION_gstreamer1.0-plugins-base ??= "${GST_VERSION}"
PREFERRED_VERSION_gstreamer1.0-plugins-good ??= "${GST_VERSION}"
PREFERRED_VERSION_gstreamer1.0-omx ??= "${GST_VERSION}"
PREFERRED_VERSION_nativesdk-orc ??= "${ORC_VERSION}"
