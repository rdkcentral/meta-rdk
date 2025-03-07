#
# RDM Agent
#

DESCRIPTION = "rdm-agent"
SECTION = "rdm-agent"
DEPENDS += "rbus"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=8700a1d105cac2a90d4f51290ac6e466"

# This tells bitbake where to find the files we're providing on the local filesystem
FILESEXTRAPATHS:prepend := "${THISDIR}:"

SRC_URI = "${CMF_GITHUB_ROOT}/rdm-agent;${CMF_GITHUB_SRC_URI_SUFFIX};name=rdmagent"
SRCREV_FORMAT = "rdmagent"

# Make sure our source directory (for the build) matches the directory structure in the tarball
S = "${WORKDIR}/git"

inherit autotools coverity systemd syslog-ng-config-gen
SYSLOG-NG_FILTER = "apps-rdm"
SYSLOG-NG_SERVICE_apps-rdm = "apps-rdm.service"
SYSLOG-NG_DESTINATION_apps-rdm = "rdm_status.log"
SYSLOG-NG_LOGRATE_apps-rdm = "high"

PARALLEL_MAKE = ""

DEPENDS += "commonutilities"
DEPENDS += "opkg"

CFLAGS:append = " -std=c11 -fPIC -D_GNU_SOURCE -Wall -Werror "

LDFLAGS:append = " -lIARMBus -lsecure_wrapper"

DEPENDS += " iarmmgrs iarmbus libsyswrapper"
