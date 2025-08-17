LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=175792518e4ac015ab6696d16c4f607e"

PV = "${RDK_RELEASE}+git${SRCPV}"

SRCREV = "d332320370379900302daaaa3d181f3b40c5ac9a"

SRC_URI += "${CMF_GIT_ROOT}/rdk/components/generic/sys_mon_tools/sys_resource;protocol=${CMF_GIT_PROTOCOL};branch=${CMF_GIT_BRANCH}"

S = "${WORKDIR}/git"
