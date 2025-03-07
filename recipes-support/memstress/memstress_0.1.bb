DESCRIPTION = "A simple application to stress memory for testing swap use cases"
SECTION = "support"

LICENSE = "Apache-2.0" 
LIC_FILES_CHKSUM = "file://LICENSE;md5=175792518e4ac015ab6696d16c4f607e"

SRC_URI = "${CMF_GIT_ROOT}/rdk/components/generic/sys_mon_tools/mem_analyser_tools;protocol=${CMF_GIT_PROTOCOL};branch=${CMF_GIT_BRANCH}"

S = "${WORKDIR}/git/memstress"

inherit autotools
