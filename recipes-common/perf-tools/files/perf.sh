#!/bin/sh

# ==========================================================
# Performance Collection Script
#
# Usage:
# sh perf.sh "syscpu,proccpu,cpu+,io" "120" "boot" "5"
#
# Arguments:
#   $1 -> metrics
#   $2 -> iterations
#   $3 -> scenario (optional)
#   $4 -> interval (optional)
#
# Config fallback:
#   /media/apps/perf.conf
#
# ==========================================================

CONFIG_FILE="/media/apps/perf.conf"

DEFAULT_INTERVAL=5
DEFAULT_SCENARIO="idle"

DEFAULT_PROCESSES="syslog-ng|systemd-journald|logrotate|systemd-cat|WPEFramework|telemetry2_0"

DEFAULT_DEVICE="mmcblk0"

LOG_PATH="/opt/logs"

# ----------------------------------------
# Load config if present
# ----------------------------------------

load_config()
{
    if [ -f "$CONFIG_FILE" ]
    then
        . "$CONFIG_FILE"
    fi
}

# ----------------------------------------
# Parse input
# ----------------------------------------

parse_inputs()
{
    METRICS_ARG="$1"
    ITERATIONS_ARG="$2"
    SCENARIO_ARG="$3"
    INTERVAL_ARG="$4"

    METRICS="${METRICS_ARG:-$METRICS}"
    SCENARIO="${SCENARIO_ARG:-$SCENARIO}"
    PROCESSES="${PROCESSES:-$DEFAULT_PROCESSES}"
    DEVICE="${DEVICE:-$DEFAULT_DEVICE}"

    INTERVAL="${INTERVAL_ARG:-${INTERVAL:-$DEFAULT_INTERVAL}}"

    [ -z "$SCENARIO" ] && SCENARIO="$DEFAULT_SCENARIO"
    ITERATIONS="${ITERATIONS_ARG:-$ITERATIONS}"

    if [ -z "$ITERATIONS" ]
    then
        echo "Usage:"
        echo "sh perf.sh \"metrics\" iterations [scenario] [interval]"
        exit 1
    fi

    if [ -z "$METRICS" ]
    then
        echo "ERROR: metrics missing"
        exit 1
    fi
}

# ----------------------------------------
# Create output folder
# ----------------------------------------

create_run_directory()
{
    RUN_DIR="/tmp/perf_run_${SCENARIO}_$(date +%Y%m%d_%H%M)"

    mkdir -p "$RUN_DIR"

    echo "Run directory: $RUN_DIR"
}

# ----------------------------------------
# Save run metadata
# ----------------------------------------

write_metadata()
{
cat > "$RUN_DIR/run_info.txt" <<EOF
Scenario=$SCENARIO
Metrics=$METRICS
Iterations=$ITERATIONS
Interval=$INTERVAL
Processes=$PROCESSES
Device=$DEVICE
StartTime=$(date)
EOF
}

# ----------------------------------------
# Collectors
# ----------------------------------------

collect_syscpu()
{
    echo "Starting mpstat..."

    mpstat -P ALL \
        $INTERVAL \
        $ITERATIONS \
        > "$RUN_DIR/mpstat.log" &
}

collect_proccpu()
{
    echo "Starting pidstat CPU..."

    pidstat -u \
        -C "$PROCESSES" \
        $INTERVAL \
        $ITERATIONS \
        > "$RUN_DIR/pidstat.log" &
}

collect_procio()
{
    echo "Starting pidstat IO..."

    pidstat -d \
        -C "$PROCESSES" \
        $INTERVAL \
        $ITERATIONS \
        > "$RUN_DIR/pidstat_io.log" &
}

collect_vmstat()
{
    echo "Starting vmstat..."

    vmstat \
        $INTERVAL \
        $ITERATIONS \
        > "$RUN_DIR/vmstat.log" &
}

collect_iostat()
{
    echo "Starting iostat..."

    iostat -xdk \
        $INTERVAL \
        $ITERATIONS \
        $DEVICE \
        > "$RUN_DIR/iostat.log" &
}

# ----------------------------------------
# Log growth tracking
# ----------------------------------------

monitor_log_size()
{
(
COUNT=0

while [ $COUNT -lt $ITERATIONS ]
do
    echo "$(date +%s) $(du -sk $LOG_PATH 2>/dev/null)"

    sleep $INTERVAL

    COUNT=$((COUNT+1))
done

) > "$RUN_DIR/log_size.log" &
}

monitor_log_count()
{
(
COUNT=0

while [ $COUNT -lt $ITERATIONS ]
do
    echo "$(date +%s) $(find $LOG_PATH -type f | wc -l)"

    sleep $INTERVAL

    COUNT=$((COUNT+1))
done

) > "$RUN_DIR/log_file_count.log" &
}

# ----------------------------------------
# Cleanup
# ----------------------------------------

cleanup()
{
    echo ""
    echo "Stopping collection..."

    pkill -P $$

    exit
}

trap cleanup INT TERM

# ----------------------------------------
# Dispatcher
# ----------------------------------------

start_metric()
{
    case "$1" in

        syscpu)
            collect_syscpu
            ;;

        proccpu)
            collect_proccpu
            ;;

        cpu+)
            collect_vmstat
            ;;

        *)
            echo "Unknown metric: $1"
            ;;
    esac
}

# ----------------------------------------
# Main
# ----------------------------------------

load_config

parse_inputs "$@"

create_run_directory

write_metadata

monitor_log_size
monitor_log_count

METRICS=$(echo "$METRICS" | tr -d ' ')

OLD_IFS="$IFS"
IFS=','

for metric in $METRICS
do
    start_metric "$metric"
done

IFS="$OLD_IFS"

wait

echo ""
echo "Data Collection complete"
echo "Logs available at:"
echo "$RUN_DIR"
