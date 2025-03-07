SUMMARY = "RDK recovery image"
LICENSE = "MIT"

# we use as few packages as possible. we only pull MACHINE ESSENTIALS
IMAGE_INSTALL = "packagegroup-core-boot mtd-utils"
IMAGE_LINGUAS = " "

# recovery images are read-only/squasfs only.
IMAGE_FSTYPES = "squashfs"
IMAGE_FEATURES += "read-only-rootfs"

inherit core-image
