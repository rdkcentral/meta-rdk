# eSDK distro Changes
INHERIT:append = " uninative image-buildinfo"
include  conf/distro/include/yocto-uninative.inc
SDK_INCLUDE_NATIVESDK = "1"
require ${@bb.utils.contains('DISTRO_FEATURES','kirkstone','setscene_enforce_ignore_tasks.inc','setscene_enforce_whitelist.inc',d)}
IMGCLASSES += "populate_sdk_ext"
SDK_EXT_TYPE ?= "full"

