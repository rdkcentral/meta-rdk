
SUMMARY = "OpenTelemetry Example Programs"
DESCRIPTION = "Example programs demonstrating OpenTelemetry OTLP gRPC logging in RDK Yocto environment"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"
FILESEXTRAPATHS:prepend := "${THISDIR}/opentelemetry-examples:"

PACKAGE_ARCH = "${MIDDLEWARE_ARCH}"

SRC_URI = "file://otlp_grpc_logging.cpp \
           file://CMakeLists.txt \
           file://otlp_logger.cpp \
           file://otlp_logger.h \
           file://test_main.cpp \
        "

S = "${WORKDIR}"

DEPENDS = "opentelemetry-cpp"
RDEPENDS_${PN} = "opentelemetry-cpp grpc protobuf"

INSANE_SKIP:${PN} += "dev-deps"

inherit cmake

# do_install() {
#     install -d ${D}${bindir}
#     install -m 0755 otlp_grpc_logging ${D}${bindir}/
# }


EXTRA_OECMAKE = "\
  -DBUILD_SHARED_LIBS=ON \
  -DWITH_ABI_VERSION_1=ON \
  -DWITH_ABI_VERSION_2=OFF \
  -DWITH_STL=OFF \
  -DCMAKE_CXX_STANDARD=17 \
"


