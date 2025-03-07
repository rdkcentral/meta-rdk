SUMMARY = "RDK IP based mediaclient (STB) Image"

REQUIRED_DISTRO_FEATURES = "ipclient rdkshell"

IMAGE_FEATURES += "splash mediaclient"

inherit rdk-image-sdk

require sdk-common.inc
require rdk-generic-media-common.inc

IMAGE_INSTALL += "packagegroup-rdk-oss-mediaclient"
#Add a uuid generator for generating random receiver id
IMAGE_INSTALL += "util-linux-uuidgen"

python __anonymous () {
    if "client" not in d.getVar('MACHINEOVERRIDES', True):
        raise bb.parse.SkipPackage("Image is meant for video client class of devices")
}
