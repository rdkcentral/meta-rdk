BPN = "eglibc"

do_install:append () {
    install -m 0644 ${WORKDIR}/SUPPORTED ${D}${datadir}/i18n/
    install -d ${D}${libdir}/locale
}

FILES:${BPN}-charmap-utf-8 += "${datadir}/i18n/SUPPORTED"
FILES:${BPN}-charmap-utf-8 += "${libdir}/locale"

