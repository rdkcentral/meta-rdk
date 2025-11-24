DESCRIPTION = "rtRemote"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=cfbe95dd83ee8f2ea75475ecc20723e5"

DEPENDS = " util-linux rtcore "

PV = "2.0.0"
PR = "r3"



S = "${WORKDIR}/git"

SRC_URI = "git://github.com/rdkcentral/rtRemote;branch=release"
SRCREV = "7e29a873d9e1a9b0102a71d812ff40a31bac10e0"

inherit cmake

FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI += "file://rtremote.conf "

ARCHFLAGS:append:arm = "${@bb.utils.contains('TUNE_FEATURES', 'callconvention-hard', '--with-arm-float-abi=hard', '--with-arm-float-abi=softfp', d)}"
ARCHFLAGS:append:mipsel = " --with-mips-arch-variant=r1"
ARCHFLAGS ?= ""
SELECTED_OPTIMIZATION:remove = "-O1"
SELECTED_OPTIMIZATION:remove = "-O2"
SELECTED_OPTIMIZATION:remove = "-Os"
SELECTED_OPTIMIZATION:append = " -O3"
SELECTED_OPTIMIZATION:append = " -Wno-deprecated-declarations -Wno-maybe-uninitialized -Wno-address"

TARGET_CFLAGS += " -fno-delete-null-pointer-checks "
TARGET_CXXFLAGS += " -fno-delete-null-pointer-checks "
TARGET_CXXFLAGS += " -Wl,--warn-unresolved-symbols "

EXTRA_OECMAKE:append:kirkstone = " -DRTREMOTE_GENERATOR_EXPORT=${WORKDIR}/build/rtRemoteConfigGen_export.cmake "
EXTRA_OECMAKE:append:dunfell = " -DRTREMOTE_GENERATOR_EXPORT=${S}/temp/rtRemoteConfigGen_export.cmake "
EXTRA_OECMAKE:append:morty = " -DRTREMOTE_GENERATOR_EXPORT=${S}/temp/rtRemoteConfigGen_export.cmake "
EXTRA_OECMAKE += " -DRT_INCLUDE_DIR=${STAGING_INCDIR}/rtcore "

do_configure:prepend:morty() {
    if [ ! -d ${S}/temp ]; then
        mkdir ${S}/temp
    fi
    cd ${S}/temp

    cmake -DCMAKE_CROSSCOMPILING=OFF -URTREMOTE_GENERATOR_EXPORT -DCMAKE_C_FLAGS=${BUILD_CFLAGS} -DCMAKE_C_COMPILER=${BUILD_CC} -DCMAKE_CXX_COMPILER=${BUILD_CXX} -DCMAKE_CXX_FLAGS=${BUILD_CXX_FLAGS} ..
    cmake --build . --target rtRemoteConfigGen
    rm -rf ${S}/temp/CMakeCache.txt
    rm -rf ${S}/temp/Makefile
    rm -rf ${S}/temp/cmake_install.cmake
    rm -rf ${S}/temp/CMakeFiles 
    cd ..
}

do_configure:prepend:dunfell() {
    if [ ! -d ${S}/temp ]; then
        mkdir ${S}/temp
    fi
    cd ${S}/temp

    cmake -DCMAKE_CROSSCOMPILING=OFF -URTREMOTE_GENERATOR_EXPORT -DCMAKE_C_FLAGS=${BUILD_CFLAGS} -DCMAKE_C_COMPILER=${BUILD_CC} -DCMAKE_CXX_COMPILER=${BUILD_CXX} -DCMAKE_CXX_FLAGS=${BUILD_CXX_FLAGS} ..
    cmake --build . --target rtRemoteConfigGen
    rm -rf ${S}/temp/CMakeCache.txt
    rm -rf ${S}/temp/Makefile
    rm -rf ${S}/temp/cmake_install.cmake
    rm -rf ${S}/temp/CMakeFiles 
    cd ..
}

do_configure:prepend:kirkstone() {

    cd ${WORKDIR}/build
    cmake -DCMAKE_CROSSCOMPILING=OFF -URTREMOTE_GENERATOR_EXPORT -DCMAKE_C_FLAGS="${BUILD_CFLAGS}" -DCMAKE_C_COMPILER="${BUILD_CC}" -DCMAKE_CXX_COMPILER="${BUILD_CXX}" -DCMAKE_CXX_FLAGS="${BUILD_CXX_FLAGS}" -S ${S}  -B ${WORKDIR}/build ..
    cmake --build . --target rtRemoteConfigGen
    rm -rf CMakeCache.txt
    rm -rf Makefile
    rm -rf cmake_install.cmake
    rm -rf CMakeFiles 
}

do_install () {
   install -d ${D}/${libdir}
   cp -a ${S}/librtRemote* ${D}/${libdir}

   mkdir -p ${D}${includedir}/rtcore
   install -m 0644 ${S}/include/rtRemote.h ${D}${includedir}/rtcore/
   install -m 0644 ${S}/include/rtRemote.h ${D}${includedir}/
   cp -R ${S}/external/rapidjson/ ${D}${includedir}/rtcore/

   mkdir -p ${D}/etc
   install -m 0644 "${WORKDIR}/rtremote.conf" "${D}/etc/"
}

FILES:${PN} += "${libdir}/*.so"
FILES_SOLIBSDEV = ""
INSANE_SKIP:${PN} += "dev-so staticdev"
INSANE_SKIP:${PN}:append:morty = " ldflags"
DEBIAN_NOAUTONAME:${PN} = "1"

BBCLASSEXTEND = "native"

PACKAGE_ARCH = "${MIDDLEWARE_ARCH}"
PACKAGE_ARCH = "${MIDDLEWARE_ARCH}"
