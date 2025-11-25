SUMMARY = "Crashupload application"
SECTION = "console/utils"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=175792518e4ac015ab6696d16c4f607e"

PACKAGE_ARCH = "${MIDDLEWARE_ARCH}"

# To have a possibility to override SRC_URI later, we are introducing the following workaround:
CRASHUPLOAD_SRC_URI ?= "${RDK_GENERIC_ROOT_GIT}/crashupload/generic;module=.;protocol=${RDK_GIT_PROTOCOL};branch=${RDK_GIT_BRANCH}"
SRC_URI = "${CMF_GITHUB_ROOT}/crashupload;${CMF_GITHUB_SRC_URI_SUFFIX};module=."
PV = "1.0.7"
S = "${WORKDIR}/git"

DEPENDS = "glib-2.0 libsyswrapper"

export LINK = "${LD}"

CFLAGS += " \
        -I=${libdir}/glib-2.0/include \
        -I=${includedir}/glib-2.0 \
        "

export GLIBS = "-lglib-2.0 -lz"
export USE_DBUS = "y"

LDFLAGS += "-Wl,-O1"

inherit coverity
inherit systemd

do_install() {
        install -d ${D}${base_libdir}/rdk
        install -d ${D}${sysconfdir} ${D}${sysconfdir}/rfcdefaults
        install -m 0755 ${S}/uploadDumps.sh ${D}${base_libdir}/rdk
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
RDEPENDS:${PN} += "busybox"

PACKAGE_BEFORE_PN += "${PN}-conf"

FILES:${PN} += "${base_libdir}/rdk/uploadDumps.sh"
FILES:${PN}:append:broadband = " ${base_libdir}/rdk/uploadDumpsUtils.sh"

SRCREV = "8e7e22d2cb988ea58b9ba9d85b8b0812c6dc77d2"
PR = "r0"
