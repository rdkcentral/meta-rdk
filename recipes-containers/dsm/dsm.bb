SUMMARY = "Downloadable Software Modules"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=50e2d278b66b3b7b20bc165c146a2b58"

S = "${WORKDIR}/git"
SRC_URI = "${CMF_GITHUB_ROOT}/DSM;protocol=${CMF_GIT_PROTOCOL};branch=${CMF_GITHUB_MAIN_BRANCH}"

SRCREV = "e2386b3ab0640fd7ffacb5f6895d05b176f63eba"
inherit pkgconfig cmake
