require recipes-core/images/rdk-generic-hybrid-wpe-image.bb

IMAGE_FEATURES += "tdk"

IMAGE_INSTALL += " \
        packagegroup-tdk \
        "
PACKAGE_EXCLUDE:pn-rdk-generic-hybrid-wpe-tdk-image = "${@bb.utils.contains('DISTRO_FEATURES','ENABLE_IPK','packagegroup-tdk','',d)}"
#To include opensource benchmark tools
IMAGE_INSTALL += "${@bb.utils.contains('DISTRO_FEATURES', 'tdk_benchmark', 'packagegroup-benchmark-tdk', '', d)}"
