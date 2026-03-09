SUMMARY = "Meminsight - memory reporting and analysis tool for RDK"
DESCRIPTION = "A C tool to capture the average memory usage of a platform including per-process memory usage and data metrics present them in a report"

LICENSE = "Apache-2.0"

LIC_FILES_CHKSUM = "file://${WORKDIR}/git/LICENSE;md5=1b8525f92b462b86205ffaba159b4481"

PV = "1.0.0"
PR = "r0"
PACKAGE_ARCH = "${MIDDLEWARE_ARCH}"

SRCREV = "0cf90e07af97b70fb1f253ebd0f71edd5a9b8225"
SRC_URI = "git://github.com/RDKCentral/MemCapture.git;branch=main;name=src;destsuffix=git"
