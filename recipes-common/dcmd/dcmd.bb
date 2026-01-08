#
# DCM Daemon
#

DESCRIPTION = "dcmd"
SECTION = "dcmd"
DEPENDS += "rbus"
DEPENDS += "commonutilities"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=2441d6cdabdc0f370be5cd8a746eb647"

# This tells bitbake where to find the files we're providing on the local filesystem
FILESEXTRAPATHS:prepend := "${THISDIR}:"

SRCREV = "f63454e4e16dc406e43138d044531b1707462ae5"
SRC_URI = "${CMF_GITHUB_ROOT}/dcm-agent;${CMF_GITHUB_SRC_URI_SUFFIX}"
PV = "1.2.0"
PR = "r0"
PACKAGE_ARCH = "${MIDDLEWARE_ARCH}"


# Make sure our source directory (for the build) matches the directory structure in the tarball
S = "${WORKDIR}/git"

inherit autotools coverity systemd syslog-ng-config-gen
SYSLOG-NG_FILTER = "dcmd"
SYSLOG-NG_SERVICE_dcmd = "dcmd.service"
SYSLOG-NG_DESTINATION_dcmd = "dcmscript.log"
SYSLOG-NG_LOGRATE_dcmd = "high"

# The autotools configuration I am basing this on seems to have a problem with a race condition when parallel make is enabled
PARALLEL_MAKE = ""
#RDKEMW-43
#method 1 ASNEDDED varible to empty
ASNEEDED = ""

CFLAGS:append = " -std=c11 -fPIC -D_GNU_SOURCE -Wall -Werror"

# added for certselector
EXTRA_OECONF:append = " --enable-t2api=yes --enable-iarmevent"
#EXTRA_OECONF:append = " --enable-mountutils=yes --enable-rdkcertselector=yes"

#DEPENDS:append = " ${@bb.utils.contains('DISTRO_FEATURES', 'safec', ' safec', " ", d)}"
#CFLAGS:append = " ${@bb.utils.contains('DISTRO_FEATURES', 'safec',  ' `pkg-config --cflags libsafec`', '-fPIC', d)}"
#CFLAGS:append = " ${@bb.utils.contains('DISTRO_FEATURES', 'safec', '', ' -DSAFEC_DUMMY_API', d)}"
#LDFLAGS:append = " ${@bb.utils.contains('DISTRO_FEATURES', 'safec', ' `pkg-config --libs libsafec`', '', d)}"
#certselector

LDFLAGS:append = "${@bb.utils.contains('DISTRO_FEATURES', 'enable_maintenance_manager', '-lIARMBus', '', d)}"
#LDFLAGS:append = "-lcurl -lrdkloggers -lRdkCertSelector -ldwnlutil -lfwutils -L$(PKG_CONFIG_SYSROOT_DIR)/usr/lib -L$(PKG_CONFIG_SYSROOT_DIR)/usr/lib64 -lrdkconfig"
#LDFLAGS:append = "-lsafec"
DEPENDS += "${@bb.utils.contains('DISTRO_FEATURES', 'enable_maintenance_manager', 'iarmmgrs iarmbus', '', d)}"

CFLAGS:append += "${@bb.utils.contains('DISTRO_FEATURES', 'enable_maintenance_manager', '-DHAS_MAINTENANCE_MANAGER', '', d)}"

#certselector
#LDFLAGS:append = " -lsafec -lsecure_wrapper"

do_install:append () {
    install -d ${D}${bindir}
    install -d ${D}${systemd_unitdir}/system
    install -m 0644 ${S}/dcmd.service ${D}${systemd_unitdir}/system
    install -d ${D}${includedir}
    install -m 0644 ${S}/uploadstblogs/include/*.h ${D}${includedir}/
    #install -m 0755 ${S}/logupload ${D}${bindir}/
}

# Add to do_compile if not using an external Makefile
#do_compile:append() {
# Compile logupload.cpp directly (adapt if you're using a Makefile)
#    ${CXX} ${S}/logupload/logupload.cpp -o ${S}/logupload/logupload.bin -lcurl -lrdkloggers -I${S}/include
#}


#do_compile:append() {
#    ${CXX} ${CPPFLAGS} ${CXXFLAGS} ${LDFLAGS} \
#        ${S}/logupload/logupload.cpp \
#        -o ${S}/logupload/logupload.bin \
#        -lcurl -lrdkloggers -lRdkCertSelector -ldwnlutil -lfwutils -L$(PKG_CONFIG_SYSROOT_DIR)/usr/lib -L$(PKG_CONFIG_SYSROOT_DIR)/usr/lib64 -lrdkconfig
#}



# Add any extra packaging if needed
FILES_${PN} += "${bindir}/logupload"
SYSTEMD_SERVICE:${PN} += "dcmd.service"
