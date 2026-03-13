
#ifndef OTLP_LOGGER_H
#define OTLP_LOGGER_H

#include <string>
#include <opentelemetry/logs/severity.h>

namespace otlp_logger
{
    // Only expose log_msg to outside
    void log_msg(opentelemetry::logs::Severity level, const std::string &msg);
    void log_msgf(opentelemetry::logs::Severity level, const char *fmt, ...);
}

#endif // OTLP_LOGGER_H
