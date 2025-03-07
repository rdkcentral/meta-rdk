SUMMARY = "RDK mediaclient Image"

IMAGE_FEATURES += "splash mediaclient"
require sdk-common.inc

require  rdk-generic-media-common.inc
inherit rdk-image-sdk

IMAGE_INSTALL += "packagegroup-rdk-oss-mediaclient"
#Add a uuid generator for generating random receiver id
IMAGE_INSTALL += "util-linux-uuidgen"

python __anonymous () {
    if "client" not in d.getVar('MACHINEOVERRIDES', True):
        raise bb.parse.SkipPackage("Image is meant for video client class of devices")
}

