SUMMARY = "A Developer image for the RDK-V yocto build which supports meta toolchain, debug tools and provides the facility of building any RDKV/opensource components in Emulator itself"

require recipes-core/images/rdk-generic-hybrid-image.bb
include recipes-extended/images/core-image-lsb-sdk.bb

IMAGE_FEATURES += "tools-sdk dev-pkgs tools-debug"
IMAGE_ROOTFS_SIZE = "3000000"

IMAGE_INSTALL:append = " \
    php \
    libmcrypt \
    bzip2 \
    nmap \
    libpcap \
    bc \
    git \
    breakpad-staticdev \
    perl-module-bin \
    glib-2.0-utils \
    gdb \
    "
IMAGE_INSTALL += " \
    packagegroup-rdk-qt5 \
    packagegroup-rdk-baserootfs \
    packagegroup-rdk-media-common \
"

IMAGE_INSTALL:append = " \
    php \
    bzip2 \
    nmap \
    bc \
    zlib \
    gstqamtunersrc \
    gdisplay \
    keyutil \
    "

IMAGE_INSTALL:append:container = " \
   gzip \
   perl \
   libcap \
   bridge-utils \
   eglibc \
   lxc \
   file \
   strace \
   "

ROOTFS_POSTPROCESS_COMMAND += "add_systemd_services; "

add_systemd_services() {
                if [ -d ${IMAGE_ROOTFS}${sysconfdir}/systemd/system/multi-user.target.wants/ ]; then
			rm -f ${IMAGE_ROOTFS}${sysconfdir}/systemd/system/multi-user.target.wants/connman.service;
                fi
		if [ ! -f ${IMAGE_ROOTFS}${sysconfdir}/systemd/system/multi-user.target.wants/lighttpd.service ]; then
                        ln -sf ${IMAGE_ROOTFS}${systemd_unitdir}/system/lighttpd.service ${IMAGE_ROOTFS}${sysconfdir}/systemd/system/multi-user.target.wants/lighttpd.service
                fi
}

IMAGE_INSTALL += " \
    qtquick1-dev \
    qtquick1-mkspecs \
    qtquick1-plugins \
    qtquick1-qmlplugins \
    qttools-dev \
    qttools-mkspecs \
    qttools-staticdev \
    qttools-tools \
    qtwebkit-dev \
    qtwebkit-mkspecs \
"

IMAGE_INSTALL += " \
    packagegroup-core-standalone-sdk-target \
    libsqlite3-dev \
    qt3d-dev \
    qt3d-mkspecs \
    qt3d-qmlplugins \
    qtbase-dev \
    qtbase-fonts \
    qtbase-tools \
    qtbase-examples \
    qtbase-mkspecs \
    qtbase-plugins \
    qtbase-staticdev \
    qtdeclarative-dev \
    qtdeclarative-mkspecs \
    qtdeclarative-staticdev \
    qtgraphicaleffects-qmlplugins \
    qtimageformats-dev \
    qtimageformats-plugins \
    qtlocation-dev \
    qtlocation-mkspecs \
    qtlocation-plugins \
    qtlocation-qmlplugins \
    qtmultimedia-dev \
    qtmultimedia-mkspecs \
    qtmultimedia-plugins \
    qtmultimedia-qmlplugins \
    qtscript-dev \
    qtscript-mkspecs \
    qtsensors-dev \
    qtsensors-mkspecs \
    qtsensors-plugins \
    qtsensors-qmlplugins \
    qtserialport-dev \
    qtserialport-mkspecs \
    qtsystems-dev \
    qtsystems-mkspecs \
    qtsystems-qmlplugins \
    qtxmlpatterns-dev \
    qtxmlpatterns-mkspecs \
"


