SUMMARY = "Utilities for RDK debug images"

LICENSE = "MIT"

inherit packagegroup

PACKAGES = "\
    packagegroup-rdk-debug \
    "

# Packages to be included in 'debug' images
# NOTE : GPLv3 packages,DO NOT INCLUDE IN PRODUCTION IMAGES
RDEPENDS:packagegroup-rdk-debug = "\
    "

RDEPENDS:packagegroup-rdk-debug += " ${@bb.utils.contains('DISTRO_FEATURES', '\
                                                                            gstreamer1', '\
                                                                            gstreamer1.0-meta-audio \
                                                                            gstreamer1.0-meta-base \
                                                                            gstreamer1.0-meta-video \
                                                                            gstreamer1.0-meta-debug \
                                                                            gstreamer1.0-libav \
                                                                            gstreamer1.0-plugins-base \
                                                                            gstreamer1.0-plugins-good \
                                                                        ', '\
                                                                            gst-meta-audio \
                                                                            gst-meta-base \
                                                                            gst-meta-video \
                                                                            gst-meta-debug \
                                                                            gst-ffmpeg \
                                                                            gst-plugins-base \
                                                                            gst-plugins-good \
                                                                        ', d)} "
