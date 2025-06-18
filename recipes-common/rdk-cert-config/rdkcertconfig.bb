SUMMARY = "A simple library for certificate selector/locator"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"

PACKAGE_ARCH = "${MIDDLEWARE_ARCH}"
PV = "1.0"

DEPENDS += "  libsyswrapper "

#code will be cloned from the following SRC_URI
SRC_URI = "${CMF_GITHUB_ROOT}/rdk-cert-config;${CMF_GITHUB_SRC_URI_SUFFIX}"

S = "${WORKDIR}/git"
B = "${WORKDIR}/git"

CFLAGS:append = " -DCONFIG_ERROR_ENABLED "
inherit autotools pkgconfig coverity
do_install() {
	  install -d ${D}${sysconfdir}/ssl/certs
    if [ "${@bb.utils.contains('DISTRO_FEATURES', 'ENABLE_HW_CERT_USAGE', 'true', 'false', d)}" = "true" ]; then
           install -Dm 0644 ${S}/CertSelector/conf/hrot.properties ${D}${sysconfdir}/ssl/certsel/hrot.properties           
    fi
}
do_install:append:client() {
    install -Dm 0644 ${S}/CertSelector/conf/certsel.cfg ${D}${sysconfdir}/ssl/certsel/certsel.cfg
}

TARGET_CC_ARCH += "${LDFLAGS}"

EXTRA_OECONF += "--enable-rdklogger"
