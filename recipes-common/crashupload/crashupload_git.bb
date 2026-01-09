SUMMARY = "Crash Upload Utility for RDK Platforms"
SECTION = "console/utils"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://../LICENSE;md5=175792518e4ac015ab6696d16c4f607e"

PV = "1.0.7"
PR = "r0"
PACKAGE_ARCH = "${MIDDLEWARE_ARCH}"

SRC_URI = "${CMF_GITHUB_ROOT}/${BPN}.git;nobranch=1;protocol=${CMF_GIT_PROTOCOL}"
SRCREV = "65f365c4a8bf2cfe7dd6e33c60742f1d33efdbc9"

S = "${WORKDIR}/git/c_sourcecode"

DEPENDS = "glib-2.0 libsyswrapper"

export LINK = "${LD}"

CFLAGS:append = " \
        -I=${libdir}/glib-2.0/include \
        -I=${includedir}/glib-2.0 \
        -DLIBRDKCERTSELECTOR \
        "

export GLIBS = "-lglib-2.0 -lz"
export USE_DBUS = "y"

LDFLAGS:append = "-Wl,-O1"

inherit autotools systemd coverity pkgconfig

do_install() {
        install -d ${D}${base_libdir}/rdk
        install -d ${D}${sysconfdir} ${D}${sysconfdir}/rfcdefaults
        install -m 0755 ${S}/uploadDumps.sh ${D}${base_libdir}/rdk
}

DEPENDS:append:client = " \
				curl \
    			openssl \
				zlib \
	    		libarchive \
    			libsyswrapper \
    			rdk-logger \
    			commonutilities \
				rdkcertconfig \
				"

do_install:append:client() {
        install -d ${D}${bindir}
        install -m 0755 ${B}/crashupload ${D}${bindir}/crashupload
}

do_install:append:broadband() {
        use_sysv="${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'false', 'true', d)}"
        $use_sysv || install -d ${D}${systemd_unitdir}/system
        $use_sysv || install -m 0644 ${S}/coredump-upload.service ${D}${systemd_unitdir}/system/
        $use_sysv || install -m 0644 ${S}/coredump-upload.path ${D}${systemd_unitdir}/system/
        $use_sysv || install -m 0644 ${S}/minidump-on-bootup-upload.service ${D}${systemd_unitdir}/system/
        $use_sysv || install -m 0644 ${S}/minidump-on-bootup-upload.timer ${D}${systemd_unitdir}/system/
        install -d ${D}${sysconfdir}
        install -m 0755 ${S}/uploadDumpsUtils.sh ${D}${base_libdir}/rdk
}

SYSTEMD_SERVICE:${PN}:append:broadband = " coredump-upload.service \
                                           coredump-upload.path \
                                           minidump-on-bootup-upload.service \
                                           minidump-on-bootup-upload.timer \
"
RDEPENDS:${PN} += "busybox commonutilities"

PACKAGE_BEFORE_PN += "${PN}-conf"

FILES:${PN}:append:client = "${bindir}/crashupload"
FILES:${PN} += "${base_libdir}/rdk/uploadDumps.sh"
FILES:${PN}:append:broadband = " ${base_libdir}/rdk/uploadDumpsUtils.sh"
