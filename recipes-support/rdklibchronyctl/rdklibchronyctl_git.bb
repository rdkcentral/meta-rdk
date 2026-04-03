SUMMARY = "This receipe install chronyc control library"
SECTION = "console/utils"
DESCRIPTION = "chrony control library"

LICENSE = "CLOSED"
#LICENSE = "Apache-2.0"
#LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

SRCREV_time-utils = "e793b6419293bce3170405d12574436153df4dd6"
SRC_URI = "${CMF_GITHUB_ROOT}/time-utils;${CMF_GITHUB_SRC_URI_SUFFIX};name=time-utils"



PACKAGE_ARCH = "${MIDDLEWARE_ARCH}"
SRCREV_FORMAT = "time-uilts"



S = "${WORKDIR}"
# Compilation directly in the recipe
do_compile() {
    # Compile object with PIC
    ${CC} ${CFLAGS} -fPIC -I${S} -c ${S}/libchronyctl.c -o ${B}/libchronyctl.o

    # Create shared library
    ${CC} ${LDFLAGS} -shared -Wl,-soname,libchronyctl.so \
        ${B}/libchronyctl.o -o ${B}/libchronyctl.so -lpthread -lm

   ${CC} ${CFLAGS} ${LDFLAGS} -I${S} ${S}/test_chronyctl.c \
        -L${B} -lchronyctl -o ${B}/test_chronyctl -lpthread -lm
}

do_install() {
    install -d ${D}${libdir}
    install -m 0755 ${B}/libchronyctl.so ${D}${libdir}
    
    install -d ${D}${includedir}
    install -m 0644 ${S}/libchronyctl.h ${D}${includedir}
    install -m 0644 ${S}/addressing.h ${D}${includedir}
    install -m 0644 ${S}/candm.h ${D}${includedir}

    install -d ${D}${bindir}
    install -m 0755 ${B}/test_chronyctl ${D}${bindir}
}

FILES:${PN} = "${libdir}/libchronyctl.so ${bindir}/test_chronyctl"
FILES:${PN}-dev = "${includedir}/libchronyctl.h ${includedir}/addressing.h ${includedir}/candm.h"

# This is a library, so we might need some rdepends if it were calling other bins
# but here it is self-contained.
RDEPENDS:${PN} = "chrony"
