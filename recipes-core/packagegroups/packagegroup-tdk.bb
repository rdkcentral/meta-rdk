SUMMARY = "Custom package group for RDK bits"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

inherit packagegroup

PACKAGES = "\
    packagegroup-tdk \
    "

#components used in TDK
RDEPENDS:packagegroup-tdk = "\
  tdk \
  ${@bb.utils.contains('DISTRO_FEATURES', 'enable_kernel_testing', 'ltp', '', d)} \
  ${@bb.utils.contains('DISTRO_FEATURES', 'enable_firebolt_compliance_tdk', 'waymetric', '', d)} \
  "

#Removed sysstat as it is not being used from TDK now.
#Removed bind-utils from RDEPENDS since it is overriding nslookup binary in RDK image and it is causing issue in getting ipv6 address.

RDEPENDS:packagegroup-tdk:append:qemuall = " alsa-conf "



