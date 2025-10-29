SUMMARY = "Memcapture - memory reporting and analysis tool for RDK"
DESCRIPTION = "A C++ tool to capture the average memory usage of a platform including per-process memory usage and data metrics present them in a report"

LICENSE = "Apache-2.0"

LIC_FILES_CHKSUM = "file://${WORKDIR}/git/LICENSE;md5=1b8525f92b462b86205ffaba159b4481"

SRC_URI = "git://github.com/RDKCentral/MemCapture.git;branch=main;name=src;destsuffix=git"

S = "${WORKDIR}/git"
B = "${WORKDIR}/build"

DEPENDS = "inja nlohmann-json"

inherit cmake syslog-ng-config-gen systemd
EXTRA_OECMAKE += "-DCMAKE_BUILD_TYPE=Release -DENABLE_CPU_IDLE_METRICS=ON"

SYSLOG-NG_FILTER = "memcapture"
SYSLOG-NG_SERVICE_memcapture = "memcapture.service"
SYSLOG-NG_DESTINATION_memcapture = "memcapture.log"
SYSLOG-NG_LOGRATE_memcapture = "high"

do_install () {
    install -d ${D}${bindir}
    install -m 4755 ${B}/MemCapture ${D}${bindir}
}

FILES:${PN} += "${bindir}/MemCapture \
               "
