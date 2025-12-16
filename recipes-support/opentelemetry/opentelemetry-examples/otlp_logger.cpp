#include "otlp_logger.h"
#include <memory>
#include <mutex>
#include "opentelemetry/logs/logger_provider.h"
#include <opentelemetry/logs/logger.h>
#include "opentelemetry/sdk/logs/provider.h"
#include "opentelemetry/sdk/logs/logger_provider.h"
#include <opentelemetry/sdk/logs/logger_provider_factory.h>
#include <opentelemetry/sdk/logs/simple_log_record_processor.h>
#include <opentelemetry/exporters/otlp/otlp_grpc_log_record_exporter.h>
#include <opentelemetry/sdk/logs/simple_log_record_processor_factory.h>
#include "opentelemetry/exporters/ostream/log_record_exporter.h"

#include "opentelemetry/logs/logger_provider.h"
#include "opentelemetry/logs/provider.h"

//To be deleted these includes - Start
#include "opentelemetry/exporters/ostream/log_record_exporter.h"
#include "opentelemetry/exporters/ostream/span_exporter_factory.h"
#include "opentelemetry/logs/logger_provider.h"
#include "opentelemetry/sdk/logs/exporter.h"
#include "opentelemetry/sdk/logs/logger_provider.h"
#include "opentelemetry/sdk/logs/logger_provider_factory.h"
#include "opentelemetry/sdk/logs/processor.h"
#include "opentelemetry/sdk/logs/provider.h"
#include "opentelemetry/sdk/logs/simple_log_record_processor_factory.h"
#include "opentelemetry/sdk/trace/exporter.h"
#include "opentelemetry/sdk/trace/processor.h"
#include "opentelemetry/sdk/trace/provider.h"
#include "opentelemetry/sdk/trace/simple_processor_factory.h"
#include "opentelemetry/sdk/trace/tracer_provider.h"
#include "opentelemetry/sdk/trace/tracer_provider_factory.h"
#include "opentelemetry/trace/tracer_provider.h"
//To be deleted these includes - End

#include <iostream>

namespace logs_api      = opentelemetry::logs;
namespace logs_sdk      = opentelemetry::sdk::logs;
namespace logs_exporter = opentelemetry::exporter::otlp;
// namespace logs_exporter = opentelemetry::exporter::logs;

namespace otlp_logger
{
    static std::once_flag init_flag;
    static nostd::shared_ptr<opentelemetry::logs::Logger> logger;

    // void InitLogger()
    // {
    // // Create ostream log exporter instance
    // auto exporter =
    //     std::unique_ptr<logs_sdk::LogRecordExporter>(new logs_exporter::OStreamLogRecordExporter);
    // auto processor = logs_sdk::SimpleLogRecordProcessorFactory::Create(std::move(exporter));

    // std::shared_ptr<opentelemetry::sdk::logs::LoggerProvider> sdk_provider(
    //     logs_sdk::LoggerProviderFactory::Create(std::move(processor)));

    // // Set the global logger provider
    // const std::shared_ptr<logs_api::LoggerProvider> &api_provider = sdk_provider;
    // logs_sdk::Provider::SetLoggerProvider(api_provider);
    // }

    static void InitLogger()
    {
        // using namespace opentelemetry::sdk::logs;
        // using namespace opentelemetry::exporter::otlp;

        auto exporter = std::unique_ptr<logs_sdk::LogRecordExporter>(new logs_exporter::OtlpGrpcLogRecordExporter());

        auto processor = logs_sdk::SimpleLogRecordProcessorFactory::Create(std::move(exporter));

        std::shared_ptr<opentelemetry::sdk::logs::LoggerProvider> sdk_provider(
            opentelemetry::sdk::logs::LoggerProviderFactory::Create(std::move(processor)));

        const std::shared_ptr<opentelemetry::logs::LoggerProvider> &api_provider = sdk_provider;

        // opentelemetry::logs::Provider::SetLoggerProvider(api_provider);
        logs_sdk::Provider::SetLoggerProvider(api_provider);

        // GetLogger returns nostd::shared_ptr<opentelemetry::logs::Logger>
        auto provider = opentelemetry::logs::Provider::GetLoggerProvider();
        std::cout << "RSABAPATHI -- provider got successfully\n";

        logger = provider->GetLogger("example_logger", "example");
        std::cout << "RSABAPATHI -- example_logger got\n";
        // logger = opentelemetry::logs::Provider::GetLoggerProvider()->GetLogger("example_logger");
    }

    void log_msg(opentelemetry::logs::Severity level, const std::string &msg)
    {
        // std::call_once(init_flag, init);
        std::call_once(init_flag, InitLogger);
        if (logger)
        {
            logger->EmitLogRecord(msg, level);
        }
    }

    // ---- helper: robust vsnprintf -> std::string ----

    static std::string vformat_printf(const char *fmt, va_list args)
    {
        // First pass: compute required size (number of chars excluding '\0')
        va_list args1;
        va_copy(args1, args);
        int needed = std::vsnprintf(nullptr, 0, fmt, args1);
        va_end(args1);

        if (needed >= 0)
        {
            // Resize to exact number of chars; we’ll write with a trailing '\0'
            std::string out;
            out.resize(static_cast<std::size_t>(needed));

            va_list args2;
            va_copy(args2, args);
            // Write into string storage via non-const pointer in C++14
            std::vsnprintf(&out[0], static_cast<std::size_t>(needed) + 1, fmt, args2);
            va_end(args2);

            return out; // excludes the final '\0'
        }

        // Fallback: grow a buffer until it fits (handles libcs that return -1)
        std::vector<char> buf(1024);
        while (true)
        {
            va_list args3;
            va_copy(args3, args);
            int n = std::vsnprintf(buf.data(), buf.size(), fmt, args3);
            va_end(args3);

            if (n >= 0 && static_cast<std::size_t>(n) < buf.size())
            {
                return std::string(buf.data(), static_cast<std::size_t>(n));
            }
            std::size_t new_size = (n >= 0) ? static_cast<std::size_t>(n + 1) : buf.size() * 2;
            buf.resize(new_size);
        }
    }

    // ---- new printf-style API ----
    void log_msgf(opentelemetry::logs::Severity level, const char *fmt, ...)
    {
        std::call_once(init_flag, InitLogger);
        std::cout << "RSABAPATHI -- checking logger\n";
        if (!logger)
            return;
        
        std::cout << "RSABAPATHI -- logger avaialble\n";

        va_list args;
        va_start(args, fmt);
        std::string msg = vformat_printf(fmt, args);
        va_end(args);

        // Use the same EmitLogRecord signature ordering as your existing code
        std::cout << "RSABAPATHI -- Emitting Log Record\n";
        logger->EmitLogRecord(msg, level);
        // If your SDK expects (Severity, Body), use:
        // logger->EmitLogRecord(level, msg);
    }

}
