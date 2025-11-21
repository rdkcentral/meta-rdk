SUMMARY = "webconfig client library"
HOMEPAGE = "https://github.com/xmidt-org/webcfg"
SECTION = "libs"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"


DEPENDS = "cjson trower-base64 msgpack-c cimplog wdmp-c curl wrp-c"
DEPENDS:append = "${@bb.utils.contains("DISTRO_FEATURES", "webconfig_bin", " rbus cpeabs", " ", d)}"
DEPENDS:append = " ${@bb.utils.contains('DISTRO_FEATURES', 'aker', ' nanomsg libparodus ', '', d)}"

SRCREV = "2c91a008bd7feeaea0c95c24eabd94cde11faa43"
SRC_URI = "git://github.com/xmidt-org/webcfg.git;protocol=https;branch=master"

RDEPENDS:${PN} += "util-linux-uuidgen"

S = "${WORKDIR}/git"

ASNEEDED = ""

inherit pkgconfig cmake ${@bb.utils.contains("DISTRO_FEATURES", "kirkstone", "python3native", "pythonnative", d)}

EXTRA_OECMAKE = "-DBUILD_TESTING=OFF -DBUILD_YOCTO=true"

EXTRA_OECMAKE += " ${@bb.utils.contains('DISTRO_FEATURES', 'webconfig_bin', '-DWEBCONFIG_BIN_SUPPORT=true', '', d)}"

EXTRA_OECMAKE += " ${@bb.utils.contains('DISTRO_FEATURES', 'aker', '-DFEATURE_SUPPORT_AKER=true', '', d)}"

EXTRA_OECMAKE += " ${@bb.utils.contains('DISTRO_FEATURES', 'webconfig_bin mqttCM', '-DFEATURE_SUPPORT_MQTTCM=true', '', d)}"

LDFLAGS += "-lcjson -lmsgpackc -ltrower-base64 -lwdmp-c -lcimplog -lcurl -lwrp-c"

LDFLAGS:append = "${@bb.utils.contains("DISTRO_FEATURES", "webconfig_bin", " -lrbus -lcpeabs ", " ", d)}"

LDFLAGS:append = " ${@bb.utils.contains('DISTRO_FEATURES', 'aker', ' -llibparodus -lnanomsg ', '', d)}"

CFLAGS:append = " ${@bb.utils.contains('DISTRO_FEATURES', 'multipartUtility', '-DMULTIPART_UTILITY', '', d)}"

CFLAGS:append = " ${@bb.utils.contains("DISTRO_FEATURES", "WanFailOverSupportEnable", " -DWAN_FAILOVER_SUPPORTED ", " ", d)} "

CFLAGS:append = " ${@bb.utils.contains('DISTRO_FEATURES', 'webconfig_bin mqttCM', '-DFEATURE_SUPPORT_MQTTCM', '', d)}"

CFLAGS:append = " \
        -DBUILD_YOCTO \
        -I${STAGING_INCDIR}/wdmp-c \
        -I${STAGING_INCDIR}/cimplog \
        -I${STAGING_INCDIR}/trower-base64 \
        -I${STAGING_INCDIR}/wrp-c \
        -fPIC \
        "
CFLAGS:append = " ${@bb.utils.contains('DISTRO_FEATURES', 'webconfig_bin', '-I${STAGING_INCDIR}/rbus -I${STAGING_INCDIR}/rtmessage', '', d)}"

CFLAGS:append = " ${@bb.utils.contains('DISTRO_FEATURES', 'aker', '-I${STAGING_INCDIR}/nanomsg -I${STAGING_INCDIR}/libparodus', '', d)}"

CFLAGS:append = " -Wno-format-truncation -Wno-sizeof-pointer-memaccess"

SRC_URI:append += " ${@bb.utils.contains('DISTRO_FEATURES', 'webconfig_bin', 'file://webconfig_broadband_metadata.json', '', d)}"
SRC_URI:append += " ${@bb.utils.contains('DISTRO_FEATURES', 'webconfig_bin', 'file://webconfig_video_metadata.json', '', d)}"
SRC_URI:append += " ${@bb.utils.contains('DISTRO_FEATURES', 'webconfig_bin', 'file://metadata_parser.py', '', d)}"
SRC_URI:append += " ${@bb.utils.contains('DISTRO_FEATURES', 'webconfig_bin', 'file://webconfig.service', '', d)}"

do_install:append:broadband() {

    if ${@bb.utils.contains("DISTRO_FEATURES", "webconfig_bin", "true", "false", d)}
    then
      if ${@bb.utils.contains("DISTRO_FEATURES", "gateway_manager", "false", "true", d)}
      then
        sed -z 's/"name": "gwfailover",\n[[:blank:]]*"bitposition": 1,\n[[:blank:]]*"support": true,/"name": "gwfailover",\n"bitposition": 1,\n"support": false,/g' ${WORKDIR}/webconfig_metadata.json > ${WORKDIR}/out.txt
        mv ${WORKDIR}/out.txt ${WORKDIR}/webconfig_metadata.json
      fi
      install -d ${D}/usr/ccsp/webconfig
      install -d ${D}/etc
      touch ${D}/etc/WEBCONFIG_ENABLE
      (${PYTHON} ${WORKDIR}/metadata_parser.py ${WORKDIR}/webconfig_metadata.json ${D}/etc/webconfig.properties ${MACHINE})
    fi

    if ${@bb.utils.contains("DISTRO_FEATURES", "WanFailOverSupportEnable", "true", "false", d)}
    then
      touch ${D}/etc/CURRENT_INTERFACE
    fi
}

# The libwebcfg.so shared lib isn't versioned, so force the .so file into the
# run-time package (and keep it out of the -dev package).

FILES_SOLIBSDEV = ""
FILES:${PN} += " ${@bb.utils.contains("DISTRO_FEATURES", "webconfig_bin", "${exec_prefix}/ccsp/webconfig ${bindir}/*", "${libdir}/*.so", d)}"

ASNEEDED_hybrid = ""
ASNEEDED_client = ""

PV = "1.1.0"
PR = "r0"
PACKAGE_ARCH = "${MIDDLEWARE_ARCH}"
