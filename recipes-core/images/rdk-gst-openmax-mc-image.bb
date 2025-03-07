SUMMARY = "RDK gst openmax mediaclient Image"

IMAGE_FEATURES += "splash mediaclient"

inherit rdk-image

IMAGE_ROOTFS_SIZE = "8192"

IMAGE_INSTALL += " \
        packagegroup-rdk-openmax \
        packagegroup-rdk-qt5 \
        packagegroup-rdk-media-common \
        ${@bb.utils.contains("DISTRO_FEATURES", "gstreamer1", "gstreamer1.0-plugins-good-isomp4", "gst-plugins-good-isomp4", d)} \
	"

