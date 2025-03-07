SUMMARY = "A console-only image for the RDK-B yocto build"

inherit rdk-image

IMAGE_FEATURES += "broadband"

IMAGE_ROOTFS_SIZE = "8192"

IMAGE_INSTALL:append = " \
    packagegroup-rdk-oss-broadband \
    packagegroup-rdk-ccsp-broadband \
    rdk-logger \
    "

IMAGE_INSTALL:append:fwupgrader = " rdkfwupgrader "

IMAGE_INSTALL:append:container = " \
   gzip \
   perl \
   libcap \
   bridge-utils \
   eglibc \
   lxc \
   file \
   strace \
   "

python __anonymous () {
    if "broadband" not in d.getVar('MACHINEOVERRIDES', True):
        raise bb.parse.SkipPackage("Image is meant for broadband class of devices")
}

do_rootfs[nostamp] = "1"
