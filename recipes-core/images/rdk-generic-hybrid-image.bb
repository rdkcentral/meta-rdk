SUMMARY = "RDK mediaserver-client hybrid image"

IMAGE_FEATURES += "splash mediaserver mediaclient"

require rdk-generic-media-common.inc

python __anonymous () {
    if "hybrid" not in d.getVar('MACHINEOVERRIDES', True):
        raise bb.parse.SkipPackage("Image is meant for video gateway class of devices")
}

