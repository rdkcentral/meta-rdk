SUMMARY = "Custom package group for TDK-B"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

inherit packagegroup

PACKAGES = "\
    packagegroup-tdk-broadband \
    "

#components used in TDK-B
RDEPENDS:packagegroup-tdk-broadband = "\
  tdk-b \
  sysstat \
  sed \
  "
