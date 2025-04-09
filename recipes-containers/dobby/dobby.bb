SUMMARY = "Dobby Container Manager"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c466d4ab8a68655eb1edf0bf8c1a8fb8"

include dobby.inc

SRC_URI:append = " file://Fix_compile_gcc11.patch  \
                   file://Add_config_header_kirkstone.patch \
                   file://dobby.generic.json \
                   file://dobby_start_after_apparmor.patch \
                   file://0001-Update-DobbyConfig.cpp.patch \
                 "

PACKAGE_ARCH = "${MIDDLEWARE_ARCH}"

DEPENDS = "${@bb.utils.contains('DISTRO_FEATURES', 'systemd', ' systemd ', '', d)} libnl dbus jsoncpp boost yajl breakpad breakpad-wrapper libsyswrapper libcap"
DEPENDS:append = " autoconf-native automake-native "
RDEPENDS:${PN} = "crun (>= 0.14.1) ${@bb.utils.contains('DISTRO_FEATURES', 'dac', '', ' dobby-thunderplugin', d)} "

CFLAGS:append = " --sysroot=${RECIPE_SYSROOT}"

python do_patch_new () {
    bb.build.exec_func('patch_do_patch', d)
}

addtask do_patch_new after do_configure before do_compile

S = "${WORKDIR}/git"

inherit pkgconfig cmake systemd logrotate_config
inherit syslog-ng-config-gen

LOGROTATE_NAME="dobby"
LOGROTATE_LOGNAME_dobby="dobby.log"
LOGROTATE_SIZE_dobby="1572864"
LOGROTATE_ROTATION_dobby="3"
LOGROTATE_SIZE_MEM_dobby="1572864"
LOGROTATE_ROTATION_MEM_dobby="3"

# Always build debug version for now
EXTRA_OECMAKE =  " -DCMAKE_BUILD_TYPE=Debug -DBUILD_REFERENCE=${SRCREV}"

SRC_URI += "file://secure_wrapper.patch"

# Enable plugins
# Logging, networking, ipc, storage, minidump, oomcrash enabled by default for all builds
PACKAGECONFIG ?= "logging networking ipc storage minidump oomcrash"

# Options for plugins
# -------------------------------------
# RDK Plugins
PACKAGECONFIG[logging]      = "-DPLUGIN_LOGGING=ON,-DPLUGIN_LOGGING=OFF,"
PACKAGECONFIG[networking]   = "-DPLUGIN_NETWORKING=ON,-DPLUGIN_NETWORKING=OFF,"
PACKAGECONFIG[ipc]          = "-DPLUGIN_IPC=ON,-DPLUGIN_IPC=OFF,"
PACKAGECONFIG[storage]      = "-DPLUGIN_STORAGE=ON,-DPLUGIN_STORAGE=OFF,"
PACKAGECONFIG[minidump]     = "-DPLUGIN_MINIDUMP=ON,-DPLUGIN_MINIDUMP=OFF,"
PACKAGECONFIG[oomcrash]     = "-DPLUGIN_OOMCRASH=ON,-DPLUGIN_OOMCRASH=OFF,"
PACKAGECONFIG[testplugin]   = "-DPLUGIN_TESTPLUGIN=ON,-DPLUGIN_TESTPLUGIN=OFF,"
PACKAGECONFIG[gpu]          = "-DPLUGIN_GPU=ON,-DPLUGIN_GPU=OFF,"
PACKAGECONFIG[localtime]    = "-DPLUGIN_LOCALTIME=ON,-DPLUGIN_LOCALTIME=OFF,"
PACKAGECONFIG[rtscheduling] = "-DPLUGIN_RTSCHEDULING=ON,-DPLUGIN_RTSCHEDULING=OFF,"
PACKAGECONFIG[httpproxy]    = "-DPLUGIN_HTTPPROXY=ON,-DPLUGIN_HTTPPROXY=OFF,"
PACKAGECONFIG[appservices]  = "-DPLUGIN_APPSERVICES=ON,-DPLUGIN_APPSERVICES=OFF,"
PACKAGECONFIG[thunder]      = "-DPLUGIN_THUNDER=ON,-DPLUGIN_THUNDER=OFF,"
PACKAGECONFIG[ionmemory]    = "-DPLUGIN_IONMEMORY=ON,-DPLUGIN_IONMEMORY=OFF,"
PACKAGECONFIG[devicemapper] = "-DPLUGIN_DEVICEMAPPER=ON,-DPLUGIN_DEVICEMAPPER=OFF,"
PACKAGECONFIG[gamepad]      = "-DPLUGIN_GAMEPAD=ON,-DPLUGIN_GAMEPAD=OFF,"

PACKAGECONFIG[legacycomponents] = "-DLEGACY_COMPONENTS=ON,-DLEGACY_COMPONENTS=OFF,ctemplate,"

# -------------------------------------

# Add the systemd service
SYSTEMD_SERVICE:${PN} = "dobby.service"

# Skip harmless QA issue caused by installing but not shipping buildtime cmake files
INSANE_SKIP:${PN} = "installed-vs-shipped"

# Ensure that the unversioned symlinks of libraries are kept (and don't generate a QA error)
INSANE_SKIP:${PN} += "dev-so"
SOLIBS = ".so"
FILES_SOLIBSDEV = ""

do_install:append(){
    install -d ${D}${sysconfdir}

    #Copy the dobby generic config file to /etc/
    install -m 0644 ${S}/../dobby.generic.json ${D}${sysconfdir}/dobby.generic.json
}

FILES:${PN} += "${sysconfdir}/dobby.generic.json"
FILES:${PN} += "${systemd_system_unitdir}/dobby.service"
FILES:${PN} += "${sysconfdir}/systemd/system/multi-user.target.wants/dobby.service"
FILES:${PN} += "${bindir}/DobbyTool"
FILES:${PN} += "${sbindir}/DobbyDaemon"
FILES:${PN} += "${libexecdir}/DobbyInit"
FILES:${PN} += "${libdir}/plugins/dobby/*.so*"
FILES:${PN} += "${libdir}/libethanlog.so*"
FILES:${PN} += "${libdir}/libocispec.so*"
PV ?= "1.0.0"
PR ?= "r0"
