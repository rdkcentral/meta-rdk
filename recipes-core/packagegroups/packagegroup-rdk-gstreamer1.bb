SUMMARY = "GStreamer 1.x packages"

LICENSE = "MIT"

inherit packagegroup

RDEPENDS:${PN} = "\
    gstreamer1.0-meta-audio \
    gstreamer1.0-meta-base \
    gstreamer1.0-meta-video \
    gstreamer1.0-libav \
    gstreamer1.0-plugins-base \
    gstreamer1.0-plugins-good \
    gstreamer1.0-plugins-good-isomp4 \
    gstreamer1.0-plugins-good-autodetect \
    gstreamer1.0-plugins-good-audioparsers \
    gstreamer1.0-plugins-good-avi \
    gstreamer1.0-plugins-good-flv \
    "
