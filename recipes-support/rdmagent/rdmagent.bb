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
SRC_URI_append = " \
  file://apps-prerdm.service \
  file://apps_rdm.path \
  file://apps-rdm.service \
  file://apps_prerdm.sh \
"
SRCREV_FORMAT = "rdmagent"

# Make sure our source directory (for the build) matches the directory structure in the tarball
S = "${WORKDIR}/git"

inherit autotools coverity systemd syslog-ng-config-gen
SYSLOG-NG_FILTER = "apps-rdm"
SYSLOG-NG_SERVICE_apps-rdm = "apps-rdm.service"
SYSLOG-NG_DESTINATION_apps-rdm = "rdm_status.log"
SYSLOG-NG_LOGRATE_apps-rdm = "high"

LOGROTATE_NAME="rdm_status"
LOGROTATE_LOGNAME_rdm_status="rdm_status.log"
LOGROTATE_SIZE_rdm_status="1572864"
LOGROTATE_ROTATION_rdm_status="3"
LOGROTATE_SIZE_MEM_rdm_status="1572864"
LOGROTATE_ROTATION_MEM_rdm_status="3"

PARALLEL_MAKE = ""

DEPENDS += "commonutilities"
DEPENDS += "opkg"
DEPENDS = "curl openssl libsyswrapper"

CFLAGS:append = " -std=c11 -fPIC -D_GNU_SOURCE -Wall -Werror "

LDFLAGS:append = " -lIARMBus -lsecure_wrapper"

DEPENDS += " iarmmgrs iarmbus libsyswrapper"

INCLUDE_DIRS = " \
    -I${STAGING_INCDIR} \
    -I${STAGING_INCDIR}/openssl \
    "
do_install:append () {
        install -d ${D}${bindir}/
        install -d ${D}${includedir}/rdmagent/
        install -d ${D}${sysconfdir}
        install -d ${D}${sysconfdir}/rdmagent/

        install -m755 ${WORKDIR}/apps_prerdm.sh ${D}/${bindir}/
        install -D -m644 ${WORKDIR}/apps-rdm.service ${D}${systemd_unitdir}/system/apps-rdm.service
        install -D -m644 ${WORKDIR}/apps_rdm.path ${D}${systemd_unitdir}/system/apps_rdm.path
        install -D -m644 ${WORKDIR}/apps-prerdm.service ${D}${systemd_unitdir}/system/apps-prerdm.service

        rm -f ${D}${sysconfdir}/rdm/kmsVerify.sh
}

SYSTEMD_SERVICE:${PN} = "apps-rdm.service"
SYSTEMD_SERVICE:${PN} += "apps_rdm.path"
SYSTEMD_SERVICE:${PN} += "apps-prerdm.service"

FILES:${PN} += "${systemd_unitdir}/system/apps-rdm.service"
FILES:${PN} += "${systemd_unitdir}/system/apps_rdm.path"
FILES:${PN} += "${systemd_unitdir}/system/apps-prerdm.service"

