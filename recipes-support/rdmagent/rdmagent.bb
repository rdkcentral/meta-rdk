#
# RDM Agent
#

DESCRIPTION = "rdm-agent"
SECTION = "rdm-agent"
DEPENDS += "rbus"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=8700a1d105cac2a90d4f51290ac6e466"

PV = "2.1.2"
PR = "r0"
PACKAGE_ARCH = "${MIDDLEWARE_ARCH}"

# This tells bitbake where to find the files we're providing on the local filesystem
FILESEXTRAPATHS:prepend := "${THISDIR}:"

SRC_URI = "${CMF_GITHUB_ROOT}/rdm-agent;${CMF_GITHUB_SRC_URI_SUFFIX};name=rdmagent"
SRCREV:pn-rdmagent = "7e09a367a5b0547fcecb215b1a4b837de86a26cc"
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

DEPENDS += "commonutilities telemetry"
DEPENDS += "opkg"

CFLAGS:append = " -std=c11 -fPIC -D_GNU_SOURCE -Wall -Werror "

LDFLAGS:append = " -lsecure_wrapper"

DEPENDS += "libsyswrapper"

EXTRA_OECONF:append = " --enable-iarmbusSupport=yes --enable-t2api=yes"

DEPENDS:append = " ${@bb.utils.contains('DISTRO_FEATURES', 'safec', ' safec', " ", d)}"
CFLAGS:append = " ${@bb.utils.contains('DISTRO_FEATURES', 'safec',  ' `pkg-config --cflags libsafec`', ' -DSAFEC_DUMMY_API', d)}"
LDFLAGS:append = " ${@bb.utils.contains('DISTRO_FEATURES', 'safec', ' `pkg-config --libs libsafec`', '', d)}"

DEPENDS:append = " iarmmgrs iarmbus"
LDFLAGS:append = " -lIARMBus"

do_install:append () {
        install -d ${D}${bindir}/
        install -d ${D}${sysconfdir}
        install -d ${D}${sysconfdir}/rdm/
        install -D -m644 ${S}/apps-rdm.service ${D}${systemd_unitdir}/system/apps-rdm.service
        install -D -m644 ${S}/apps_rdm.path ${D}${systemd_unitdir}/system/apps_rdm.path
        install -D -m755 ${S}/scripts/getRdmDwldPath.sh ${D}${sysconfdir}/rdm/getRdmDwldPath.sh
        install -D -m755 ${S}/scripts/downloadUtils.sh ${D}${sysconfdir}/rdm/downloadUtils.sh
        install -D -m755 ${S}/scripts/loggerUtils.sh ${D}${sysconfdir}/rdm/loggerUtils.sh
        install -D -m600 ${S}/rdm-manifest.json ${D}${sysconfdir}/rdm/rdm-manifest.json
}

SYSTEMD_SERVICE:${PN} = "apps-rdm.service"
SYSTEMD_SERVICE:${PN} += "apps_rdm.path"

FILES:${PN} += "${systemd_unitdir}/system/apps-rdm.service"
FILES:${PN} += "${systemd_unitdir}/system/apps_rdm.path"
FILES:${PN} += "${sysconfdir}/rdm/* "
