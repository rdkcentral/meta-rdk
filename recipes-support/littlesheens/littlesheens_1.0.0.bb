SUMMARY = "A minimalist Machines implementation in ECMAScript that's executed by Duktape and wrapped in a C library"
DESCRIPTION = "A minimalist Machines implementation in ECMAScript that's executed by Duktape and wrapped in a C library"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"

SECTION = "libs"

DEPENDS = "duktape"
DEPENDS:append = " vim-native coreutils-native"
DEPENDS:remove_morty = " vim-native coreutils-native"

S = "${WORKDIR}/git"

SRC_URI = "git://github.com/Comcast/littlesheens.git;protocol=https;branch=master"
SRCREV = "${AUTOREV}"
PV := "${PV}+${SRCPV}"

# We must tell cmake where the headers are
# instead of relying on CMAKE_INSTALL_PREFIX as
# the OE build system will set the prefix to
# "${prefix} (which is "/usr") instead of the
# actual staging directory. Then on install
# it will override the install prefix with
# the staging directory.
#
# Confused yet? Yeah me too.
CFLAGS:append = " -I${STAGING_INCDIR}/duktape"

inherit pkgconfig cmake

EXTRA_OECMAKE = "-DTARGET_PLATFORM=yocto"
