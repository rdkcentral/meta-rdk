SUMMARY = "A simple hello world application"
SECTION = "console/testapp"
LICENSE = "Apache-2.0"

SRC_URI = "file://hello.c"
SRC_URI += "file://run-hello-app.sh"

S = "${WORKDIR}"

do_compile () {
    ${CC} hello.c -o hello ${CFLAGS} ${LDFLAGS}
}

do_install () {
    install -d ${D}${bindir}
    install -m 755 ${S}/hello ${D}${bindir}
    install -m 755 ${S}/run-hello-app.sh ${D}${bindir}
}

