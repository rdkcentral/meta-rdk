SUMMARY = "A simple message queue application"
SECTION = "console/testapp"
LICENSE = "Closed"

SRC_URI = "file://utility.c"
SRC_URI += "file://msgq_receive.c"

S = "${WORKDIR}"

DEPENDS += "gcc-sanitizers"
RDEPENDS:${PN} += "libasan"
CFLAGS += "-fsanitize=address -fsanitize-recover=address -I${STAGING_EXECPREFIXDIR}/lib/gcc/${TARGET_SYS}/9.5.0/include"
LDFLAGS += " -fsanitize=address -fsanitize-recover=address -lasan"
CXXFLAGS += "-fsanitize=address -fsanitize-recover=address -I${STAGING_EXECPREFIXDIR}/lib/gcc/${TARGET_SYS}/9.5.0/include"
TARGET_CC_ARCH += "${LDFLAGS}"

do_compile () {
    ${CC} utility.c -o leakCheck_utility -lpthread -lrt
    ${CC} -fPIC ${CFLAGS} -DMSGQ_CREATE --shared -lrt -lpthread -Wl,-soname,libmsgq.so.0 ${LDFLAGS} -o libmsgq.so.0.0.0 msgq_receive.c
}

do_install () {
    install -d ${D}${bindir}
    install -m 755 ${S}/leakCheck_utility ${D}${bindir}
    install -d ${D}${libdir}
    install -m 755 ${S}/libmsgq.so.0.0.0 ${D}${libdir}/libmsgq.so.0.0.0
    cd ${D}${libdir}
    ln -sf libmsgq.so.0.0.0 libmsgq.so.0
    ln -sf libmsgq.so.0.0.0 libmsgq.so
}

FILES:${PN} += "${bindir}/leakCheck_utility"
