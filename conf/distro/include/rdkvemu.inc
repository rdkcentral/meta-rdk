
PREFERRED_VERSION_lighttpd = "1.4.45"
PREFERRED_VERSION_smcroute = "2.0.0"
PREFERRED_VERSION_log4c = "1.2.3"

BBMASK .= "|.*recipes-ccsp|.*recipes-rdkb|.*webpabroadband|.*netflix|.*cef|.*tdk-b"
BBMASK .= "|.meta-rdk-comcast/recipes-common/nvm-sqlite/nvm-sqlite.bb|.meta-rdk-comcast/recipes-common/rdkversion/rdkversion_1.0.bb"

# add rdkfwupgrader.bb to bbmask till such time that it supports video devices.
BBMASK .= "|.meta-rdk-comcast/recipes-extended/rdkfwupgrader/rdkfwupgrader.bb"

# Build flags to control default SecAPI engine for netflix component
DISTRO_FEATURES:append = " netflix_cryptanium"

DISTRO_FEATURES:append = " playreadycdmi_cryptanium"
DISTRO_FEATURES:append = " NEW_HTTP_SERVER_DISABLE"
