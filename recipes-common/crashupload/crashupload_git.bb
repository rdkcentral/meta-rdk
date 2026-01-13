SUMMARY = "Crash Upload Utility for RDK Platforms"
SECTION = "console/utils"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://../LICENSE;md5=175792518e4ac015ab6696d16c4f607e"

PV = "1.0.7"
PR = "r0"
PACKAGE_ARCH = "${MIDDLEWARE_ARCH}"

SRC_URI = "${CMF_GITHUB_ROOT}/${BPN}.git;nobranch=1;protocol=${CMF_GIT_PROTOCOL}"
SRCREV = "e034ad84d1c628cab4c45225a1508e6be19a5336"

S = "${WORKDIR}/git/c_sourcecode"

DEPENDS = "glib-2.0 libsyswrapper"

export LINK = "${LD}"

CFLAGS:append = " \
        -I=${libdir}/glib-2.0/include \
        -I=${includedir}/glib-2.0 \
        -DLIBRDKCERTSELECTOR \
        -DRFC_API_ENABLED \
        "
#-DT2_EVENT_ENABLED \
#"

export GLIBS = "-lglib-2.0 -lz"
export USE_DBUS = "y"

LDFLAGS:append = "-Wl,-O1"

LDFLAGS += "-lrfcapi -ltelemetry_msgsender"

inherit autotools systemd coverity pkgconfig syslog-ng-config-gen logrotate_config

SYSLOG-NG_FILTER = "crashupload"
SYSLOG-NG_SERVICE_crashupload = "minidump-secure-upload.service minidump-upload.service coredump-secure-upload.service coredump-upload.service"
SYSLOG-NG_DESTINATION_crashupload = "core_log.txt"
SYSLOG-NG_LOGRATE_crashupload = "high"

LOGROTATE_NAME="crashupload"
LOGROTATE_LOGNAME_crashupload="core_log.txt"
#HDD_ENABLE
LOGROTATE_SIZE_crashupload="128000"
LOGROTATE_ROTATION_crashupload="3"
#HDD_DISABLE
LOGROTATE_SIZE_MEM_crashupload="128000"
LOGROTATE_ROTATION_MEM_crashupload="3"

DEPENDS:append:client = " \
				curl \
    			openssl \
				zlib \
	    		libarchive \
    			libsyswrapper \
    			rdk-logger \
    			commonutilities \
				rfc \
				telemetry \
				"
RDEPENDS:${PN} += "busybox commonutilities telemetry"

do_install() {
        install -d ${D}${base_libdir}/rdk
        install -d ${D}${sysconfdir} ${D}${sysconfdir}/rfcdefaults
		install -m 0755 ${WORKDIR}/git/runDumpUpload.sh ${D}${base_libdir}/rdk
        install -m 0755 ${WORKDIR}/git/uploadDumps.sh ${D}${base_libdir}/rdk
}

do_install:append:client() {
        install -d ${D}${bindir}
        install -m 0755 ${B}/src/crashupload ${D}${bindir}/crashupload
}

do_install:append:broadband() {
        use_sysv="${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'false', 'true', d)}"
        $use_sysv || install -d ${D}${systemd_unitdir}/system
        $use_sysv || install -m 0644 ${WORKDIR}/git/coredump-upload.service ${D}${systemd_unitdir}/system/
        $use_sysv || install -m 0644 ${WORKDIR}/git/coredump-upload.path ${D}${systemd_unitdir}/system/
        $use_sysv || install -m 0644 ${WORKDIR}/git/minidump-on-bootup-upload.service ${D}${systemd_unitdir}/system/
        $use_sysv || install -m 0644 ${WORKDIR}/git/minidump-on-bootup-upload.timer ${D}${systemd_unitdir}/system/
        install -d ${D}${sysconfdir}
        install -m 0755 ${WORKDIR}/git/uploadDumpsUtils.sh ${D}${base_libdir}/rdk
}

SYSTEMD_SERVICE:${PN}:append:broadband = " coredump-upload.service \
                                           coredump-upload.path \
                                           minidump-on-bootup-upload.service \
                                           minidump-on-bootup-upload.timer \
										"

PACKAGE_BEFORE_PN += "${PN}-conf"

FILES:${PN}:append:client = " ${bindir}/crashupload"
FILES:${PN}:append = " ${base_libdir}/rdk/uploadDumps.sh"
FILES:${PN}:append = " ${base_libdir}/rdk/runDumpUpload.sh"
FILES:${PN}:append:broadband = " ${base_libdir}/rdk/uploadDumpsUtils.sh"
