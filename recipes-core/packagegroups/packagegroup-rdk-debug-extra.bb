SUMMARY = "Extra utilities for RDK debug images"

LICENSE = "MIT"

inherit packagegroup

PACKAGES = "\
    packagegroup-rdk-debug-extra \
    "

# Packages to be included in 'debug-extra' images
RDEPENDS:packagegroup-rdk-debug-extra = "\
    strace \
    tcf-agent \
    alsa-utils-amixer \
    alsa-utils-aplay \
    ldd \
    qtbase-examples \
    systemd-analyze \
    "
