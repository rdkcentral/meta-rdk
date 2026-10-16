
#include <memory>
#include <opentelemetry/logs/provider.h>
#include <opentelemetry/logs/logger.h>
#include <opentelemetry/sdk/logs/logger_provider_factory.h>
#include <opentelemetry/sdk/logs/simple_log_record_processor.h>
#include <opentelemetry/exporters/otlp/otlp_grpc_log_record_exporter.h>

using namespace opentelemetry::logs;
using namespace opentelemetry::sdk::logs;
using namespace opentelemetry::exporter::otlp;

int main()
{
    auto exporter = std::unique_ptr<LogRecordExporter>(new OtlpGrpcLogRecordExporter());
    auto processor = std::unique_ptr<LogRecordProcessor>(new SimpleLogRecordProcessor(std::move(exporter)));

    // Factory returns std::unique_ptr<sdk::logs::LoggerProvider>
    auto provider_unique = LoggerProviderFactory::Create(std::move(processor));

    // Disambiguate conversion to nostd::shared_ptr<logs::LoggerProvider>
    opentelemetry::nostd::shared_ptr<opentelemetry::logs::LoggerProvider> provider{
        provider_unique.release()  // release raw pointer from unique_ptr
    };

    Provider::SetLoggerProvider(provider);

    auto logger = Provider::GetLoggerProvider()->GetLogger("example_logger");
    logger->EmitLogRecord("Hello from OTLP gRPC Logging Client!", Severity::kInfo);

    return 0;
}
