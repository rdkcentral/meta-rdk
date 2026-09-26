
cmake_minimum_required(VERSION 3.10)
project(otlp_grpc_logging_example)

set(CMAKE_CXX_STANDARD 14)

find_package(opentelemetry-cpp CONFIG REQUIRED)

add_executable(otlp_grpc_logging otlp_grpc_logging.cpp)

target_link_libraries(otlp_grpc_logging
    opentelemetry_logs
    opentelemetry_exporter_otlp_grpc_log
)


set(LIB_VERSION "1.0.0")
set(LIB_SOVERSION "1")

add_library(otlp_logger SHARED otlp_logger.cpp)

set_target_properties(otlp_logger PROPERTIES
    VERSION ${LIB_VERSION}
    SOVERSION ${LIB_SOVERSION}
)

target_include_directories(otlp_logger PUBLIC ${CMAKE_CURRENT_SOURCE_DIR})
target_link_libraries(otlp_logger PUBLIC 
    opentelemetry_common
    opentelemetry_logs
    opentelemetry_exporter_otlp_grpc_log
    opentelemetry_exporter_ostream_logs
)

# Install the library
install(TARGETS otlp_logger
        DESTINATION ${CMAKE_INSTALL_LIBDIR})

# Install the header
install(FILES otlp_logger.h
        DESTINATION ${CMAKE_INSTALL_INCLUDEDIR})

add_executable(otlp-logger-example test_main.cpp)
target_link_libraries(otlp-logger-example PRIVATE otlp_logger)

# Install the example executable
install(TARGETS otlp-logger-example
        RUNTIME DESTINATION ${CMAKE_INSTALL_BINDIR})



