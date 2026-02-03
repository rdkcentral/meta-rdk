SUMMARY = "Crash Upload Utility for RDK Platforms"
SECTION = "console/utils"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://../LICENSE;md5=175792518e4ac015ab6696d16c4f607e"

PV = "2.0.0"
PR = "r0"
PACKAGE_ARCH = "${MIDDLEWARE_ARCH}"

SRC_URI = "${CMF_GITHUB_ROOT}/${BPN}.git;nobranch=1;protocol=${CMF_GIT_PROTOCOL}"
#SRCREV = "d62af2db9d7748268353476fcda95c3b342c5428"
SRCREV = "19c74d72b4f9299c7e2d124fd8ddb5e8b5265a5f"

S = "${WORKDIR}/git/c_sourcecode"

DEPENDS = "glib-2.0 libsyswrapper"

export LINK = "${LD}"

CFLAGS:append = " \
                -I=${libdir}/glib-2.0/include \
                -I=${includedir}/glib-2.0 \
                -DLIBRDKCERTSELECTOR \
                -DRFC_API_ENABLED \
                -DT2_EVENT_ENABLED \
                -DRDK_LOGGER \
                -DUSE_EXTENDED_LOGGER_INIT \
                "

export GLIBS = "-lglib-2.0 -lz"
export USE_DBUS = "y"

LDFLAGS:append = "-Wl,-O1"
LDFLAGS:append = " -lrfcapi -ltelemetry_msgsender -lRdkCertSelector -lrdkconfig"

inherit autotools systemd coverity pkgconfig

DEPENDS:append:client = " \
                        curl \
                        openssl \
                        zlib \
                        libarchive \
                        libsyswrapper \
                        rdk-logger \
                        commonutilities \
                        rdkcertconfig \
                        rfc \
                        telemetry \
                        "

do_install() {
        install -d ${D}${base_libdir}/rdk
        install -d ${D}${sysconfdir} ${D}${sysconfdir}/rfcdefaults
        install -m 0755 ${WORKDIR}/git/uploadDumps.sh ${D}${base_libdir}/rdk
        install -m 0755 ${WORKDIR}/git/runDumpUpload.sh ${D}${base_libdir}/rdk
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

RDEPENDS:${PN} += "busybox commonutilities"

PACKAGE_BEFORE_PN += "${PN}-conf"

FILES:${PN}:append:client = " ${bindir}/crashupload"
FILES:${PN}:append = " ${base_libdir}/rdk/uploadDumps.sh"
FILES:${PN}:append = " ${base_libdir}/rdk/runDumpUpload.sh"
FILES:${PN}:append:broadband = " ${base_libdir}/rdk/uploadDumpsUtils.sh"

