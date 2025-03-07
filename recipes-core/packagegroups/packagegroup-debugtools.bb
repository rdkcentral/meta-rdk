SUMMARY = "Custom package group for Debugtools"

LICENSE = "MIT"

inherit packagegroup

PACKAGES = "\
    packagegroup-debugtools \
    "

#To install the script for downloading debugtools
RDEPENDS:${PN} = "\
  debug-tools \
  "

#Debug tools and its dependencies
DEPENDS = "\
  ${@bb.utils.contains('DISTRO_FEATURES','debugtools_disable_gpertools',' ','binutils',d)}  \
  ${@bb.utils.contains('DISTRO_FEATURES','debugtools_disable_lsof',' ','lsof',d)}  \ 
  ${@bb.utils.contains('DISTRO_FEATURES','debugtools_disable_gpertools',' ','gperftools',d)} \
  ${@bb.utils.contains('DISTRO_FEATURES','debugtools_disable_strace',' ','strace',d)}  \
  ${@bb.utils.contains('DISTRO_FEATURES','debugtools_disable_gdb',' ','gdb',d)}     \
  "