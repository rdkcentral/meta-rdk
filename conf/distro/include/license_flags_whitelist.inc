#This file will be included for yocto versions morty and daisy.
LICENSE_FLAGS_ACCEPTED += "commercial_gst-fluendo-mpegdemux \
                            commercial_${MLPREFIX}gst-ffmpeg \
                            ${@bb.utils.contains('DISTRO_FEATURES', 'gstreamer1_18','commercial_${MLPREFIX}ffmpeg','commercial_${MLPREFIX}libav',d)} \
                            commercial_libomxil \
                            commercial_gst-openmax \
                            commercial_mpg123 \
                            commercial_${MLPREFIX}x264 \
                            commercial_gstreamer1.0-omx \
                            commercial_${MLPREFIX}gstreamer1.0-libav"


