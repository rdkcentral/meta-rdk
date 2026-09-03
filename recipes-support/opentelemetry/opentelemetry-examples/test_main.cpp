
#include "otlp_logger.h"
#include <opentelemetry/logs/severity.h>

#include <iostream>

int main()
{
    std::cout << "RSABAPATHI -- Logs to be sent now\n";
    otlp_logger::log_msgf(opentelemetry::logs::Severity::kInfo, "First log message triggers init(): %d\n", __LINE__);
    otlp_logger::log_msgf(opentelemetry::logs::Severity::kError, "Second log message after init: %d", __LINE__);
    std::cout << "RSABAPATHI -- Logs are sent\n";
    return 0;
}
