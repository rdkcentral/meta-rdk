SUMMARY = "A console-only image for the RDK-C yocto build"

inherit rdk-image-sdk

IMAGE_FEATURES += "package-management camera"

IMAGE_ROOTFS_SIZE = "8192"

IMAGE_INSTALL:append += " \
    packagegroup-rdk-oss-camera \
    packagegroup-rdk-ccsp-camera \
    rdk-logger \
	"
python __anonymous () {
    if "camera" not in d.getVar('MACHINEOVERRIDES', True):
        raise bb.parse.SkipPackage("Image is meant for camera class of devices")
}


