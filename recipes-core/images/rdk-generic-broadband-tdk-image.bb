SUMMARY = "A console-only image for the RDK-B yocto build with TDK enabled"

inherit rdk-image

require rdk-generic-broadband-image.bb

IMAGE_FEATURES += "tdk"

IMAGE_INSTALL:append = " tdk-b"

IMAGE_INSTALL += " \
        packagegroup-tdk-broadband \
        "
