SUMMARY = "A console-only image for the RDK-B yocto build which supports meta toolchain, debug tools and provides the facility of building any ccsp/opensource components in Emulator itself"

require recipes-core/images/rdk-generic-broadband-image.bb
include recipes-extended/images/core-image-lsb-sdk.bb

IMAGE_INSTALL:append += " \
    ccsp-webui-csrf \
    ${@bb.utils.contains('DISTRO_FEATURES', 'webui_php', 'ccsp-webui-php', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'webui_jst', 'ccsp-webui-jst', '', d)} \  
    php \
    jst \
    libmcrypt \
    bzip2 \
    nmap \
    libpcap \
    bc \
    openvswitch \
    git \
    breakpad-staticdev \
    perl-module-bin \
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

