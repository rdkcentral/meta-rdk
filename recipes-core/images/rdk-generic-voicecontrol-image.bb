SUMMARY = "A console-only image for the RDK-X yocto build"

inherit rdk-image

IMAGE_FEATURES[validitems] += "voicecontrol"
IMAGE_FEATURES += "voicecontrol"

python __anonymous () {
    if "rdkx" not in d.getVar('OVERRIDES', True):
        raise bb.parse.SkipPackage("Image is meant for rdkx class of devices")
}
