SUMMARY = "A console-only image for the RDK-C yocto build with TDK enabled"

IMAGE_FEATURES += "tdk"

inherit rdk-image

require rdk-generic-camera-image.bb

IMAGE_INSTALL:append = " tdk-c"

IMAGE_INSTALL += " \
        packagegroup-tdk-camera \
        "

