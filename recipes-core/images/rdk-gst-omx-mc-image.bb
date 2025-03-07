SUMMARY = "RDK gst omx mediaclient Image"

IMAGE_FEATURES += "splash mediaclient"

inherit rdk-image

IMAGE_ROOTFS_SIZE = "8192"

IMAGE_INSTALL += " \
        packagegroup-rdk-omx \
        packagegroup-rdk-qt5 \
        packagegroup-rdk-media-common \
	"

