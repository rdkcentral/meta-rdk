#Currently moving only firebolt related content to common space
#This is imported by both hybrid and mediaclient video devices.
#So all common code can be moved to this place.

TOOLCHAIN_TARGET_TASK:append  =" ${MLPREFIX}breakpad-staticdev ${MLPREFIX}alsa-dev ${MLPREFIX}egl-headers"
USER_PREFERRED_SDK_INSTALL_DIR = "./RNE-3.0"
#changes made for populating SDK
TOOLCHAIN_OUTPUTNAME="${MACHINE}-RNE-SDK-${SDK_VERSION}"
SDKIMAGE_FEATURES:remove ="dbg-pkgs"
SDKIMAGE_FEATURES:remove ="src-pkgs"
POPULATE_SDK_POST_TARGET_COMMAND += 'do_rdk_artifact_customizations; '

TOOLCHAIN_HOST_TASK:append:daisy = "\
   nativesdk-python-core \
   nativesdk-python-textutils \
   nativesdk-python-sqlite3 \
   nativesdk-python-pickle \
   nativesdk-python-logging \
   nativesdk-python-elementtree \
   nativesdk-python-curses \
   nativesdk-python-compile \
   nativesdk-python-compiler \
   nativesdk-python-fcntl \
   nativesdk-python-shell \
   nativesdk-python-misc \
   nativesdk-python-multiprocessing \
   nativesdk-python-subprocess \
   nativesdk-python-xmlrpc \
   nativesdk-python-netclient \
   nativesdk-python-netserver \
   nativesdk-python-distutils \
   nativesdk-python-unixadmin \
   nativesdk-python-compression \
   nativesdk-python-json \
   nativesdk-python-unittest \
   nativesdk-python-mmap \
   nativesdk-python-difflib \
   nativesdk-python-pprint \
   nativesdk-python-git \
   nativesdk-python-pkgutil \
   "
TOOLCHAIN_HOST_TASK:append = " nativesdk-bison"
TOOLCHAIN_TARGET_TASK:append  =" ${MLPREFIX}devicesettings-hal-headers "

#Rust SDK
TOOLCHAIN_HOST_TASK:append = " ${@'packagegroup-rust-cross-canadian-${MACHINE}' if os.path.isfile('${RDKROOT}/meta-rust/conf/layer.conf') else ''}"

#eSDK changes
include  ${@bb.utils.contains('DISTRO_FEATURES', 'enable_esdk', 'recipes-core/images/rdk-esdk-override.inc', 'xxx.inc', d)}
DEPENDS:append = " ${@bb.utils.contains('DISTRO_FEATURES', 'enable_esdk', ' patchelf-native ', '', d)} "

