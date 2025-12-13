SUMMARY = "Dobby Thunder Plugin - allows containers to access Thunder via \
           JSON-RPC and generate security token"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${WORKDIR}/git/LICENSE;md5=c466d4ab8a68655eb1edf0bf8c1a8fb8"

include dobby.inc

PACKAGE_ARCH = "${MIDDLEWARE_ARCH}"

DEPENDS = "dobby wpeframework-clientlibraries"

S = "${WORKDIR}/git"
OECMAKE_SOURCEPATH = "${S}/rdkPlugins/Thunder"

inherit pkgconfig cmake

# Always build debug version for now
EXTRA_OECMAKE =  " -DCMAKE_BUILD_TYPE=Debug -DEXTERNAL_THUNDER_PLUGIN_BUILD=ON"

# Skip harmless QA issue caused by installing but not shipping buildtime cmake files
INSANE_SKIP:${PN} = "installed-vs-shipped"

# Ensure that the unversioned symlinks of libraries are kept (and don't generate a QA error)
INSANE_SKIP:${PN} += "dev-so"
SOLIBS = ".so"
FILES_SOLIBSDEV = ""

FILES:${PN} += "${libdir}/plugins/dobby/*.so*"

PV = "3.14.1"
PR = "r0"
