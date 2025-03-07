# Headless disable graphical splash
SPLASH = ""

PREFERRED_VERSION_nodejs = "0.10.38"
PREFERRED_VERSION_nodejs-native = "0.10.38"
PREFERRED_VERSION_smcroute = "2.0.0"

PREFERRED_VERSION_openssl = "1.1.0g"

VOLATILE_BINDS:append = "/tmp/ccsp_msg.cfg /ccsp_msg.cfg\n"

BBMASK .= "|.meta-rdk/recipes-extended|.*recipes-qt|.meta-rdk-comcast/recipes-extended|meta-rdk/recipes-core/images/rdk-oss-image.bb|meta-rdk/recipes-core/images/rdk-generic-image.bb|.meta-rdk/recipes-multimedia|.meta-rdk/recipes-graphics|.meta-rdk/recipes-oem|.meta-rdk-comcast/recipes-multimedia|.meta-rdk-comcast/recipes-graphics|.meta-rdk/recipes-thirdparty|.meta-rdk-ext/recipes-graphics|.meta-rdk-ext/recipes-utils"

BBMASK .= "|.*recipes-ccsp|.*recipes-rdkb|.*webpabroadband"

# add rbus-core.bb and rtmessage_git.bb to bbmask till such time that it supports video devices
BBMASK .= "|.meta-rdk/recipes-common/rbus-core/rbus-core.bb"
BBMASK .= "|.meta-rdk-comcast/recipes-common/rtmessage/rtmessage_git.bbappend"

# add rdkfwupgrader.bb to bbmask till such time that it supports camera devices.
BBMASK .= "|.meta-rdk-comcast/recipes-extended/rdkfwupgrader/rdkfwupgrader.bb"

# parodus bbappend is supported for video device.
BBMASK .= "|./meta-rdk-comcast-video/recipes-support/parodus/parodus_%.bbappend"

# add rdkversion_1.0.bb is not supported for Camera devices.
BBMASK .= "|./meta-rdk/recipes-common/rdkversion/"

# add webconfig-framework.bb to bbmask till such time that it supports Camera devices.
BBMASK .= "|.meta-rdk/recipes-common/webconfig-framework/webconfig-framework.bb"

DISTRO_FEATURES:remove = "zeroconf"

OPTIMIZE_DEFAULT = "-Os"

VIDEO_CAMERA = "1"
