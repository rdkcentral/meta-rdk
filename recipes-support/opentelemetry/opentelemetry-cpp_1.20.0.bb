
SUMMARY = "OpenTelemetry C++ SDK and API"
DESCRIPTION = "C++ implementation of OpenTelemetry for traces, metrics, and logs."
HOMEPAGE = "https://github.com/open-telemetry/opentelemetry-cpp"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"

PV = "1.20.0"
SRC_URI = "gitsm://github.com/open-telemetry/opentelemetry-cpp.git;branch=main"
SRCREV = "6175aa0b213eea053247e43b4f35b8d201fa356e"

PACKAGE_ARCH = "${MIDDLEWARE_ARCH}"

FILESEXTRAPATHS:prepend := "${THISDIR}/opentelemetry:"
# Bring in patch that links absl::strings from Abseil
SRC_URI += "file://0001-link-absl-strings-into-common.patch"

S = "${WORKDIR}/git"

inherit cmake pkgconfig

DEPENDS = "\
    grpc \
    protobuf \
    abseil-cpp \
    nlohmann-json \
    curl \
    re2 \
    c-ares \
    openssl \
"

DEPENDS += "protobuf-native grpc-native"

PACKAGECONFIG ??= "otlp_grpc"
PACKAGECONFIG[otlp_grpc]      = "-DWITH_OTLP_GRPC=ON,-DWITH_OTLP_GRPC=OFF,grpc protobuf"
PACKAGECONFIG[otlp_http]      = "-DWITH_OTLP_HTTP=ON,-DWITH_OTLP_HTTP=OFF,curl nlohmann-json"
PACKAGECONFIG[otlp_prometheus]= "-DWITH_PROMETHEUS=ON,-DWITH_PROMETHEUS=OFF,prometheus-cpp"
PACKAGECONFIG[benchmark]      = "-DWITH_BENCHMARK=ON,-DWITH_BENCHMARK=OFF,benchmark"
PACKAGECONFIG[testing]        = "-DWITH_TESTING=ON,-DWITH_TESTING=OFF,gtest"

EXTRA_OECMAKE += "\
    -DBUILD_SHARED_LIBS=ON \
    -DWITH_ABSEIL=OFF \
    -DWITH_STL=CXX17 \
    -Dabsl_DIR=${RECIPE_SYSROOT}/usr/lib/cmake/absl \
"

# Point CMake at native protoc + grpc_cpp_plugin
EXTRA_OECMAKE += "\
  -DProtobuf_PROTOC_EXECUTABLE=${STAGING_BINDIR_NATIVE}/protoc \
  -DgRPC_CPP_PLUGIN=${STAGING_BINDIR_NATIVE}/grpc_cpp_plugin \
"

EXTRA_OECMAKE += "-DCMAKE_CXX_FLAGS_append=' -D_GLIBCXX_USE_CXX11_ABI=1'"
TARGET_LDFLAGS:append = " -Wl,--no-as-needed"



EXTRA_OECMAKE += "\
  -DBUILD_SHARED_LIBS=ON \
  -DWITH_ABI_VERSION_1=ON \
  -DWITH_ABI_VERSION_2=OFF \
  -DWITH_STL=OFF \
  -DCMAKE_CXX_STANDARD=17 \
"

# Treat unversioned .so as runtime libs
SOLIBS = ".so"
FILES:${PN} += "${libdir}/*.so"

# Prevent Yocto from classifying them as -dev
SOLIBSDEV = ""
INSANE_SKIP:${PN} += "dev-so"

# Explicitly define -dev content so it does NOT include ${libdir}/*.so
FILES:${PN}-dev = "\
    ${includedir} \
    ${libdir}/pkgconfig \
    ${libdir}/cmake \
"
