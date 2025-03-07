SUMMARY = "RDK firebolt mediaclient TDK Image"


IMAGE_FEATURES += "splash mediaclient"
IMAGE_FEATURES += "tdk"

include sdk-common.inc
IMAGE_FEATURES += "firebolt"
require  rdk-generic-media-common.inc
inherit rdk-image-sdk


IMAGE_INSTALL += "packagegroup-rdk-oss-mediaclient"

IMAGE_INSTALL += " \
        packagegroup-tdk \
        "
IMAGE_INSTALL += "${@bb.utils.contains('DISTRO_FEATURES', 'tdk_benchmark', 'packagegroup-benchmark-tdk', '', d)}"

PACKAGE_EXCLUDE:pn-rdk-generic-mediaclient-tdk-image = "${@bb.utils.contains('DISTRO_FEATURES','ENABLE_IPK','packagegroup-tdk','',d)}"

python __anonymous () {
    if "client" not in d.getVar('MACHINEOVERRIDES', True):
        raise bb.parse.SkipPackage("Image is meant for video client class of devices")
}

