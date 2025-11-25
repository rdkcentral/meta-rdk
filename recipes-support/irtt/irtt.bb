SUMMARY = "IRTT measures round-trip time, one-way delay and other metrics using UDP packets sent on a fixed period, and produces both user and machine parseable output."
HOMEPAGE = "https://${GO_IMPORT}"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://src/${GO_IMPORT}/LICENSE;md5=b234ee4d69f5fce4486a80fdaf4a4263"
SRC_URI = "git://${GO_IMPORT};branch=master;protocol=https"
SRCREV = "c5ed230a672f5233f4a87b2a28dd6d5c86b6feba"


inherit go-mod

GO_IMPORT = "github.com/heistp/irtt"
GO_INSTALL = "${GO_IMPORT}"
GO_LINKSHARED = ""
GOBUILDFLAGS:remove = "-buildmode=pie"
GO_EXTLDFLAGS:append = " -s -w "
export GOPROXY = "https://proxy.golang.org,direct"

PV="0.9.1"
PACKAGES = "${PN}-dbg ${PN}"
FILES:${PN} += "/usr/bin/irtt"
FILES:${PN}-dbg += "/usr/bin/.debug/irtt"

do_compile() {
    export CGO_ENABLED=0
    export CGO_CFLAGS="${CFLAGS}"
    export CGO_LDFLAGS="${LDFLAGS}"
    export GOARCH="${TARGET_ARCH}"
    export GOOS=linux
    export GOROOT="${STAGING_DIR_NATIVE}/usr/lib/go"
    export GOPATH="${S}"
    export GOCACHE="${WORKDIR}/go-cache"
    export GOMODCACHE="${WORKDIR}/go-mod"

    cd ${S}/src/${GO_IMPORT}/cmd/irtt
    ${GO} build ${GO_LINKSHARED} ${GOBUILDFLAGS} -o irtt main.go
}

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${S}/src/${GO_IMPORT}/cmd/irtt/irtt ${D}${bindir}/irtt
}
