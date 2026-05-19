SUMMARY = "Neo software update utility"
DESCRIPTION = "A sample C utility that checks for software updates."
LICENSE = "MIT"

LIC_FILES_CHKSUM = "file://LICENSE;md5=a0d72aad1ae907a6a8e2bb938cd28fb0"

SRC_URI = "git://github.com/gomathishankar37/neo-maintenancemanager.git;branch=main;protocol=https"

# May 19, 2026
SRCREV = "9366e5c91649505f26d4c43dc4b58df5188c9eac"
PV = "1.0.0"
PR = "r0"

S = "${WORKDIR}/git"

PACKAGE_ARCH = "${MIDDLEWARE_ARCH}"

do_compile() {
    oe_runmake
}

do_install() {
    oe_runmake install DESTDIR=${D} PREFIX=${prefix}
}

FILES:${PN} += "${bindir}/neo-softwareupdate"