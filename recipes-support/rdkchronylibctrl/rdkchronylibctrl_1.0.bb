FILESEXTRAPATHS:prepend := "${THISDIR}/files:"
SUMMARY = "Chrony Control Shared Library"
DESCRIPTION = "A thread-safe library to control chronyd via its binary protocol"
LICENSE = "CLOSED"
#LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = " \
    file://libchronyctl.c \
    file://libchronyctl.h \
    file://candm.h \
    file://addressing.h \
"

S = "${WORKDIR}"

# Compilation directly in the recipe
do_compile() {
    ${CC} ${CFLAGS} ${LDFLAGS} -Wl,-soname,libchronyctl.so -shared \
        -I${S}/include ${S}/libchronyctl.c -o ${B}/libchronyctl.so -lpthread -lm
}

do_install() {
    install -d ${D}${libdir}
    install -m 0755 ${B}/libchronyctl.so ${D}${libdir}
    
    install -d ${D}${includedir}
    install -m 0644 ${S}/include/libchronyctl.h ${D}${includedir}
}

FILES:${PN} = "${libdir}/libchronyctl.so"
FILES:${PN}-dev = "${includedir}/libchronyctl.h"

# This is a library, so we might need some rdepends if it were calling other bins
# but here it is self-contained.
RDEPENDS:${PN} = "chrony"

RDEPENDS:${PN} = "chrony"
