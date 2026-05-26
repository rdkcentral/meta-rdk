# systimemgr Library Integration Verification

This directory contains tools to verify the rdkchronylibctrl library integration with systimemgr.

## Quick Start

### On Target Device

Run the automated verification script:

```bash
./verify_library_linkage.sh
```

Or specify a custom binary path:

```bash
./verify_library_linkage.sh /path/to/custom/sysTimeMgr
```

### Manual Verification

Use the quick reference commands in `VERIFICATION_COMMANDS.txt`:

```bash
# One-liner to check all three aspects
ls /usr/lib/libchronyctl.so && \
ldd /usr/bin/sysTimeMgr | grep chrony && \
cat /proc/$(pidof sysTimeMgr)/maps | grep chrony
```

## Files in This Directory

### systimemgr_git.bb
The main BitBake recipe for systimemgr with rdkchronylibctrl integration.

**Key integration lines:**
- `DEPENDS = "... rdkchronylibctrl"` - Build-time dependency
- `LDFLAGS:append = " -lchronyctl"` - Link-time flag
- `RDEPENDS:${PN} += "... rdkchronylibctrl"` - Runtime dependency

### verify_library_linkage.sh
Automated verification script that runs 8 different tests:
1. Binary existence check
2. Library file existence check
3. Binary dependencies (ldd)
4. ELF NEEDED entries (readelf)
5. Process running check
6. Runtime memory maps (/proc/PID/maps)
7. Process memory map (pmap)
8. Package installation (opkg/rpm)

**Usage:**
```bash
./verify_library_linkage.sh
```

**Expected output:** All checks should pass (✓ PASS)

### VERIFICATION_COMMANDS.txt
Quick reference card with:
- Common verification commands
- One-liner checks
- Troubleshooting guide
- Expected integration patterns

## Verification Methods Summary

### Build-Time (Before Deployment)
- `ldd` - List dynamic dependencies
- `readelf -d` - Check ELF NEEDED entries
- `objdump -p` - Display program headers
- `nm -D` - Check symbol references

### Runtime (On Target Device)
- `/proc/PID/maps` - Memory mapped regions
- `pmap` - Process memory map
- `pldd` - Process libraries
- `ls /usr/lib/libchronyctl.so` - File existence

### Build System
- BitBake task logs
- Recipe dependency checks
- Package installation verification

## Expected Results

When properly integrated, you should see:

1. **Binary depends on library:**
   ```
   $ ldd /usr/bin/sysTimeMgr | grep chrony
   libchronyctl.so => /usr/lib/libchronyctl.so (0x...)
   ```

2. **Library file exists:**
   ```
   $ ls -la /usr/lib/libchronyctl.so
   -rwxr-xr-x 1 root root 12345 ... /usr/lib/libchronyctl.so
   ```

3. **Library loaded at runtime:**
   ```
   $ cat /proc/$(pidof sysTimeMgr)/maps | grep chrony
   7f... r-xp ... /usr/lib/libchronyctl.so
   ```

## Troubleshooting

### Library not in ldd output?
❌ **Problem:** Missing link-time dependency  
✅ **Solution:** Verify `LDFLAGS:append = " -lchronyctl"` in recipe

### Library file doesn't exist?
❌ **Problem:** Missing runtime dependency  
✅ **Solution:** Verify `RDEPENDS:${PN} += "... rdkchronylibctrl"` in recipe

### Build fails - headers not found?
❌ **Problem:** Missing build-time dependency  
✅ **Solution:** Verify `DEPENDS = "... rdkchronylibctrl"` in recipe

### Library in ldd but not in /proc/maps?
⚠️ **Possible:** Lazy loading - functions not called yet  
✅ **Try:** `LD_BIND_NOW=1 /usr/bin/sysTimeMgr &`

## Complete Integration Checklist

For complete library integration, the recipe MUST have:

- [ ] `DEPENDS = "... rdkchronylibctrl"` - Build-time
- [ ] `LDFLAGS:append = " -lchronyctl"` - Link-time  
- [ ] `RDEPENDS:${PN} += "... rdkchronylibctrl"` - Runtime

**All three are required!**

## Additional Documentation

- See `../../LIBRARY_LINKAGE_VERIFICATION.md` for comprehensive guide
- See `VERIFICATION_COMMANDS.txt` for quick command reference

## Example: Complete Verification Workflow

```bash
# 1. Check recipe integration
grep -E "DEPENDS|LDFLAGS|RDEPENDS" systimemgr_git.bb

# 2. After build, check binary
ldd /path/to/build/sysTimeMgr | grep chrony

# 3. On target device
./verify_library_linkage.sh

# 4. If running, check runtime
PID=$(pidof sysTimeMgr)
cat /proc/$PID/maps | grep chrony
```

All steps should succeed for proper integration! ✅
