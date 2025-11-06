DESCRIPTION = "CPU Proc Analyzer"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=175792518e4ac015ab6696d16c4f607e"

SRCREV = "8fb410c461543ffa1d97245e9c493762d9392ad4"
SRC_URI = "${CMF_GIT_ROOT}/rdk/components/generic/cpuprocanalyzer;protocol=${CMF_GIT_PROTOCOL};branch=${CMF_GIT_BRANCH};name=cpuprocanalyzer" 
S = "${WORKDIR}/git"

DEPENDS = "rdk-logger cimplog"
RDEPENDS:${PN} = "rdk-logger"

inherit autotools pkgconfig systemd coverity syslog-ng-config-gen logrotate_config
SYSLOG-NG_FILTER = "cpuprocanalyzer"
SYSLOG-NG_SERVICE_cpuprocanalyzer = "cpuprocanalyzer.service"
SYSLOG-NG_DESTINATION_cpuprocanalyzer = "cpuprocanalyzer.log"
SYSLOG-NG_LOGRATE_cpuprocanalyzer = "low"

LOGROTATE_NAME="cpuprocanalyzer"
LOGROTATE_LOGNAME_cpuprocanalyzer="cpuprocanalyzer.log"
#HDD_ENABLE
LOGROTATE_SIZE_cpuprocanalyzer="128000"
LOGROTATE_ROTATION_cpuprocanalyzer="3"
#HDD_DISABLE
LOGROTATE_SIZE_MEM_cpuprocanalyzer="128000"
LOGROTATE_ROTATION_MEM_cpuprocanalyzer="3"

do_install:append() {
        install -d ${D}${systemd_unitdir}/system ${D}${sysconfdir}
        install -m 0644 ${S}/conf/cpuprocanalyzer.service ${D}${systemd_unitdir}/system
        install -m 0644 ${S}/conf/cpuprocanalyzer.path ${D}${systemd_unitdir}/system
        install -m 0644 ${S}/conf/procanalyzerconfig.ini ${D}/etc
}

do_install:append:broadband() {
        install -d ${D}${base_libdir}/rdk ${D}{sysconfdir}
        install -m 0755 ${S}/conf/RunCPUProcAnalyzer.sh ${D}${base_libdir}/rdk
}

#SYSTEMD_SERVICE:${PN}  = "cpuprocanalyzer.service"
SYSTEMD_SERVICE:${PN} += "cpuprocanalyzer.path"

FILES:${PN} += "${systemd_unitdir}/system/cpuprocanalyzer.service"
FILES:${PN} += "${systemd_unitdir}/system/cpuprocanalyzer.path"
FILES:${PN} += "/etc/procanalyzerconfig.ini"
FILES:${PN}:append:broadband = " ${base_libdir}/rdk/RunCPUProcAnalyzer.sh"
