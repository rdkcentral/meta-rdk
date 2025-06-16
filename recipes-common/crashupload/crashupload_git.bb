SUMMARY = "Crashupload application"
SECTION = "console/utils"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=175792518e4ac015ab6696d16c4f607e"

PACKAGE_ARCH = "${MIDDLEWARE_ARCH}"

CRASHUPLOAD_SRC_URI ?= "${RDK_GENERIC_ROOT_GIT}/crashupload/generic;module=.;protocol=${RDK_GIT_PROTOCOL};branch=feature-sample-rust"
SRC_URI = "${CMF_GITHUB_ROOT}/crashupload;${CMF_GITHUB_SRC_URI_SUFFIX};module=."

PV = "1.0"
S = "${WORKDIR}/git"

inherit cargo coverity systemd syslog-ng-config-gen logrotate_config

SRCREV = "e5e6dcc682c82d991a1868cd1889e5937ce3c9f0"
S = "${WORKDIR}/git"
CARGO_SRC_DIR = "crash-upload"

SRC_URI += " \
    crate://crates.io/android-tzdata/0.1.1 \
    crate://crates.io/android_system_properties/0.1.5 \
    crate://crates.io/autocfg/1.4.0 \
    crate://crates.io/bumpalo/3.18.1 \
    crate://crates.io/cc/1.2.26 \
    crate://crates.io/cfg-if/1.0.1 \
    crate://crates.io/chrono/0.4.41 \
    crate://crates.io/core-foundation-sys/0.8.7 \
    crate://crates.io/iana-time-zone-haiku/0.1.2 \
    crate://crates.io/iana-time-zone/0.1.63 \
    crate://crates.io/js-sys/0.3.77 \
    crate://crates.io/libc/0.2.172 \
    crate://crates.io/log/0.4.27 \
    crate://crates.io/num-traits/0.2.19 \
    crate://crates.io/once_cell/1.21.3 \
    crate://crates.io/proc-macro2/1.0.95 \
    crate://crates.io/quote/1.0.40 \
    crate://crates.io/rustversion/1.0.21 \
    crate://crates.io/shlex/1.3.0 \
    crate://crates.io/syn/2.0.102 \
    crate://crates.io/unicode-ident/1.0.18 \
    crate://crates.io/wasm-bindgen-backend/0.2.100 \
    crate://crates.io/wasm-bindgen-macro-support/0.2.100 \
    crate://crates.io/wasm-bindgen-macro/0.2.100 \
    crate://crates.io/wasm-bindgen-shared/0.2.100 \
    crate://crates.io/wasm-bindgen/0.2.100 \
    crate://crates.io/windows-core/0.61.2 \
    crate://crates.io/windows-implement/0.60.0 \
    crate://crates.io/windows-interface/0.59.1 \
    crate://crates.io/windows-link/0.1.2 \
    crate://crates.io/windows-result/0.3.4 \
    crate://crates.io/windows-strings/0.4.2 \
"

SYSLOG-NG_FILTER = "crashupload"
SYSLOG-NG_SERVICE_crashupload = "coredump-upload.service"
SYSLOG-NG_SERVICE_crashupload = "minidump-on-bootup-upload.service"
SYSLOG-NG_DESTINATION_crashupload = "core_log.txt"
SYSLOG-NG_LOGRATE_crashupload = "high"

LOGROTATE_NAME = "crashupload"
LOGROTATE_LOGNAME_crashupload = "core_log.txt"
LOGROTATE_SIZE_crashupload = "512000"
LOGROTATE_ROTATION_crashupload = "2"
LOGROTATE_SIZE_MEM_crashupload = "512000"
LOGROTATE_ROTATION_MEM_crashupload = "2"

do_install:append() {
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
        #install -m 0755 ${S}/uploadDumpsUtils.sh ${D}${base_libdir}/rdk
}

SYSTEMD_SERVICE:${PN}:append:broadband = " coredump-upload.service \
                                           coredump-upload.path \
                                           minidump-on-bootup-upload.service \
                                           minidump-on-bootup-upload.timer \
"
RDEPENDS:${PN} += "busybox"

PACKAGE_BEFORE_PN += "${PN}-conf"

FILES:${PN} += "${base_libdir}/rdk/uploadDumps.sh"
#FILES:${PN}:append:broadband = " ${base_libdir}/rdk/uploadDumpsUtils.sh"
