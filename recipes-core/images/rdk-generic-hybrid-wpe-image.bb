require recipes-core/images/rdk-generic-hybrid-image.bb

IMAGE_INSTALL += " \
               westeros \
               wpe-webkit \
               westeros-init \
               wpe-webkit-init \
               virtual/vendor-westeros-sink \
	        "
ROOTFS_POSTPROCESS_COMMAND += "disable_systemd_services; "

disable_systemd_services() {
        if [ -d ${IMAGE_ROOTFS}${sysconfdir}/systemd/system/multi-user.target.wants/ ]; then
                rm -f ${IMAGE_ROOTFS}${sysconfdir}/systemd/system/multi-user.target.wants/rdkbrowser.service;

        fi
}

