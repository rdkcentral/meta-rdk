#!/bin/sh
################################################################################
# Script: verify_library_linkage.sh
# Purpose: Verify libchronyctl is properly linked to systimemgr
# Usage: ./verify_library_linkage.sh [binary_path]
################################################################################

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Default binary path
BINARY="${1:-/usr/bin/sysTimeMgr}"
LIBRARY="libchronyctl.so"
LIBRARY_PATH="/usr/lib/${LIBRARY}"

echo "================================================================================"
echo "Library Linkage Verification for systimemgr"
echo "================================================================================"
echo ""
echo "Binary: ${BINARY}"
echo "Expected Library: ${LIBRARY}"
echo ""

# Track overall status
OVERALL_STATUS=0

################################################################################
# Test 1: Check if binary exists
################################################################################
echo "[ Test 1 ] Checking if binary exists..."
if [ -f "${BINARY}" ]; then
    echo -e "${GREEN}✓ PASS${NC}: Binary exists at ${BINARY}"
else
    echo -e "${RED}✗ FAIL${NC}: Binary not found at ${BINARY}"
    OVERALL_STATUS=1
fi
echo ""

################################################################################
# Test 2: Check if library file exists
################################################################################
echo "[ Test 2 ] Checking if library file exists..."
if [ -f "${LIBRARY_PATH}" ]; then
    echo -e "${GREEN}✓ PASS${NC}: Library exists at ${LIBRARY_PATH}"
    ls -lh "${LIBRARY_PATH}"
else
    echo -e "${RED}✗ FAIL${NC}: Library not found at ${LIBRARY_PATH}"
    echo "   Possible cause: Missing RDEPENDS in recipe"
    OVERALL_STATUS=1
fi
echo ""

################################################################################
# Test 3: Check binary dependencies with ldd
################################################################################
echo "[ Test 3 ] Checking binary dependencies (ldd)..."
if command -v ldd >/dev/null 2>&1; then
    if ldd "${BINARY}" 2>/dev/null | grep -q "${LIBRARY}"; then
        echo -e "${GREEN}✓ PASS${NC}: Binary depends on ${LIBRARY}"
        ldd "${BINARY}" | grep "${LIBRARY}"
    else
        echo -e "${RED}✗ FAIL${NC}: Binary does NOT depend on ${LIBRARY}"
        echo "   Possible cause: Missing -lchronyctl in LDFLAGS"
        OVERALL_STATUS=1
    fi
else
    echo -e "${YELLOW}⊘ SKIP${NC}: ldd command not available"
fi
echo ""

################################################################################
# Test 4: Check ELF NEEDED entries (if readelf available)
################################################################################
echo "[ Test 4 ] Checking ELF NEEDED entries (readelf)..."
if command -v readelf >/dev/null 2>&1; then
    if readelf -d "${BINARY}" 2>/dev/null | grep NEEDED | grep -q "${LIBRARY}"; then
        echo -e "${GREEN}✓ PASS${NC}: Binary has NEEDED entry for ${LIBRARY}"
        readelf -d "${BINARY}" | grep NEEDED | grep "${LIBRARY}"
    else
        echo -e "${RED}✗ FAIL${NC}: Binary missing NEEDED entry for ${LIBRARY}"
        echo "   Possible cause: Missing -lchronyctl in LDFLAGS"
        OVERALL_STATUS=1
    fi
else
    echo -e "${YELLOW}⊘ SKIP${NC}: readelf command not available"
fi
echo ""

################################################################################
# Test 5: Check if process is running
################################################################################
echo "[ Test 5 ] Checking if systimemgr process is running..."
PID=$(pidof sysTimeMgr 2>/dev/null || pidof systimemgr 2>/dev/null || echo "")
if [ -n "${PID}" ]; then
    echo -e "${GREEN}✓ PASS${NC}: Process is running (PID: ${PID})"
    ps -p ${PID} -o pid,user,args
else
    echo -e "${YELLOW}⊘ WARN${NC}: Process is not running"
    echo "   Cannot perform runtime checks (tests 6-7)"
fi
echo ""

################################################################################
# Test 6: Check runtime memory maps (if process is running)
################################################################################
if [ -n "${PID}" ]; then
    echo "[ Test 6 ] Checking runtime memory maps (/proc/${PID}/maps)..."
    if [ -f "/proc/${PID}/maps" ]; then
        if grep -q "${LIBRARY}" "/proc/${PID}/maps"; then
            echo -e "${GREEN}✓ PASS${NC}: Library is loaded in process memory"
            grep "${LIBRARY}" "/proc/${PID}/maps"
        else
            echo -e "${RED}✗ FAIL${NC}: Library NOT loaded in process memory"
            echo "   Possible causes:"
            echo "   1. Library functions not called yet (lazy loading)"
            echo "   2. Missing RDEPENDS in recipe"
            echo "   Try: LD_BIND_NOW=1 ${BINARY} to force immediate loading"
            OVERALL_STATUS=1
        fi
    else
        echo -e "${YELLOW}⊘ SKIP${NC}: Cannot access /proc/${PID}/maps"
    fi
    echo ""
fi

################################################################################
# Test 7: Check with pmap (if available and process is running)
################################################################################
if [ -n "${PID}" ]; then
    echo "[ Test 7 ] Checking with pmap..."
    if command -v pmap >/dev/null 2>&1; then
        if pmap "${PID}" 2>/dev/null | grep -q "${LIBRARY}"; then
            echo -e "${GREEN}✓ PASS${NC}: Library visible in pmap"
            pmap "${PID}" | grep "${LIBRARY}"
        else
            echo -e "${RED}✗ FAIL${NC}: Library NOT visible in pmap"
            OVERALL_STATUS=1
        fi
    else
        echo -e "${YELLOW}⊘ SKIP${NC}: pmap command not available"
    fi
    echo ""
fi

################################################################################
# Test 8: Check package installation (if opkg available)
################################################################################
echo "[ Test 8 ] Checking package installation..."
if command -v opkg >/dev/null 2>&1; then
    if opkg list-installed 2>/dev/null | grep -q rdkchronylibctrl; then
        echo -e "${GREEN}✓ PASS${NC}: rdkchronylibctrl package is installed"
        opkg list-installed | grep rdkchronylibctrl
    else
        echo -e "${RED}✗ FAIL${NC}: rdkchronylibctrl package NOT installed"
        echo "   Possible cause: Missing RDEPENDS in recipe"
        OVERALL_STATUS=1
    fi
elif command -v rpm >/dev/null 2>&1; then
    if rpm -qa 2>/dev/null | grep -q rdkchronylibctrl; then
        echo -e "${GREEN}✓ PASS${NC}: rdkchronylibctrl package is installed (rpm)"
        rpm -qa | grep rdkchronylibctrl
    else
        echo -e "${RED}✗ FAIL${NC}: rdkchronylibctrl package NOT installed"
        OVERALL_STATUS=1
    fi
else
    echo -e "${YELLOW}⊘ SKIP${NC}: Package manager (opkg/rpm) not available"
fi
echo ""

################################################################################
# Summary
################################################################################
echo "================================================================================"
echo "VERIFICATION SUMMARY"
echo "================================================================================"
echo ""

if [ ${OVERALL_STATUS} -eq 0 ]; then
    echo -e "${GREEN}✓✓✓ ALL CHECKS PASSED ✓✓✓${NC}"
    echo ""
    echo "The library linkage is correctly configured:"
    echo "  • Binary has library as dependency (build-time linkage)"
    echo "  • Library file exists on filesystem (deployment)"
    if [ -n "${PID}" ]; then
        echo "  • Library is loaded in running process (runtime)"
    fi
    echo ""
    echo "Integration is COMPLETE! ✅"
else
    echo -e "${RED}✗✗✗ SOME CHECKS FAILED ✗✗✗${NC}"
    echo ""
    echo "Please review the failed tests above and check:"
    echo "  1. DEPENDS includes rdkchronylibctrl (build-time)"
    echo "  2. LDFLAGS includes -lchronyctl (link-time)"
    echo "  3. RDEPENDS includes rdkchronylibctrl (runtime)"
    echo ""
    echo "See LIBRARY_LINKAGE_VERIFICATION.md for troubleshooting."
fi

echo "================================================================================"
exit ${OVERALL_STATUS}
