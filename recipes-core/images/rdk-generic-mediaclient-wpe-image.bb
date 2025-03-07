require recipes-core/images/rdk-generic-mediaclient-image.bb

IMAGE_INSTALL += " \
               westeros \
               ${WPEWEBKIT} \
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

sdk_ext_postinst:append() {
   echo "ln -s $target_sdk_dir/layers/openembedded-core/meta-rdk $target_sdk_dir/layers/openembedded-core/../meta-rdk" >> $env_setup_script 
   echo "ln -s $target_sdk_dir/layers/openembedded-core/meta-rdk-video $target_sdk_dir/layers/openembedded-core/../meta-rdk-video" >> $env_setup_script
}
