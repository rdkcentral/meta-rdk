# Library Linkage Verification Guide

This guide provides multiple methods to verify that the rdkchronylibctrl library is properly linked to the systimemgr binary.

## Table of Contents
1. [Static Analysis (Build-time)](#static-analysis-build-time)
2. [Runtime Verification (On Target Device)](#runtime-verification-on-target-device)
3. [Build System Verification](#build-system-verification)
4. [Quick Reference](#quick-reference)

---

## Static Analysis (Build-time)

These methods check the binary **before deployment** to the target device.

### 1. Using `ldd` (List Dynamic Dependencies)

**Description**: Shows all shared libraries that a binary needs to run.

**Command**:
```bash
ldd /path/to/sysTimeMgr
```

**Expected Output** (if linked correctly):
```
linux-vdso.so.1 =>  (0x00007fff123ab000)
libchronyctl.so => /usr/lib/libchronyctl.so (0x00007f1234567000)
libWPEFrameworkPowerController.so => /usr/lib/libWPEFrameworkPowerController.so (...)
libc.so.6 => /lib/x86_64-linux-gnu/libc.so.6 (0x00007f1234000000)
...
```

**What to look for**: A line containing `libchronyctl.so`

**If NOT linked**: You won't see `libchronyctl.so` in the output.

---

### 2. Using `readelf` (Read ELF Headers)

**Description**: Displays detailed ELF binary information, including NEEDED entries.

**Command**:
```bash
readelf -d /path/to/sysTimeMgr | grep NEEDED
```

**Expected Output** (if linked correctly):
```
0x0000000000000001 (NEEDED)             Shared library: [libchronyctl.so]
0x0000000000000001 (NEEDED)             Shared library: [libWPEFrameworkPowerController.so]
0x0000000000000001 (NEEDED)             Shared library: [libc.so.6]
...
```

**What to look for**: An entry with `[libchronyctl.so]`

**Alternative command** (more verbose):
```bash
readelf -d /path/to/sysTimeMgr
```

Look for the `Dynamic section` and `NEEDED` entries.

---

### 3. Using `objdump` (Object Dump)

**Description**: Displays information from object files.

**Command**:
```bash
objdump -p /path/to/sysTimeMgr | grep NEEDED
```

**Expected Output** (if linked correctly):
```
NEEDED               libchronyctl.so
NEEDED               libWPEFrameworkPowerController.so
NEEDED               libc.so.6
...
```

**What to look for**: `NEEDED               libchronyctl.so`

---

### 4. Using `nm` (Name List)

**Description**: Lists symbols from object files. Useful to check if chrony-related symbols are referenced.

**Command**:
```bash
nm -D /path/to/sysTimeMgr | grep chrony
```

**Expected Output** (if using chrony functions):
```
U chronyctl_init
U chronyctl_cleanup
U chronyctl_get_offset
U chronyctl_makestep
...
```

**What to look for**: Undefined symbols (marked with `U`) from libchronyctl

**Note**: This shows the binary **references** chrony functions. The `U` means "undefined here, to be resolved by a shared library."

---

## Runtime Verification (On Target Device)

These methods check the binary **while it's running** on the target device.

### 5. Using `/proc/PID/maps`

**Description**: Shows memory-mapped regions for a running process, including loaded libraries.

**Commands**:
```bash
# First, find the PID
ps -ef | grep systimemgr

# Then check the maps
cat /proc/<PID>/maps | grep chrony
```

**Example**:
```bash
ps -ef | grep systimemgr
# Output: root     14968     1  0 10:50 ?        00:00:00 /usr/bin/sysTimeMgr

cat /proc/14968/maps | grep chrony
```

**Expected Output** (if loaded correctly):
```
7f1234567000-7f1234568000 r-xp 00000000 b3:02 12345  /usr/lib/libchronyctl.so
7f1234568000-7f1234768000 ---p 00001000 b3:02 12345  /usr/lib/libchronyctl.so
7f1234768000-7f1234769000 r--p 00001000 b3:02 12345  /usr/lib/libchronyctl.so
7f1234769000-7f123476a000 rw-p 00002000 b3:02 12345  /usr/lib/libchronyctl.so
```

**What to look for**: Entries containing `libchronyctl.so`

**If NOT loaded**: No output (empty result)

---

### 6. Using `ldd` on Target

**Description**: Same as build-time ldd, but run on the target device.

**Command**:
```bash
ldd /usr/bin/sysTimeMgr | grep chrony
```

**Expected Output**:
```
libchronyctl.so => /usr/lib/libchronyctl.so (0x00007f1234567000)
```

**If NOT linked**: No output

---

### 7. Using `pmap` (Process Memory Map)

**Description**: Reports memory map of a running process.

**Command**:
```bash
pmap <PID> | grep chrony
```

**Example**:
```bash
pmap 14968 | grep chrony
```

**Expected Output**:
```
00007f1234567000    64K r-x-- libchronyctl.so
00007f1234577000  2044K ----- libchronyctl.so
00007f1234778000     4K r---- libchronyctl.so
00007f1234779000     4K rw--- libchronyctl.so
```

**What to look for**: Entries showing `libchronyctl.so`

---

### 8. Using `pldd` (Process LDD)

**Description**: Lists dynamic shared objects linked into a running process.

**Command**:
```bash
pldd <PID>
```

**Example**:
```bash
pldd 14968
```

**Expected Output**:
```
14968:  /usr/bin/sysTimeMgr
/usr/lib/libchronyctl.so
/usr/lib/libWPEFrameworkPowerController.so
/lib/x86_64-linux-gnu/libc.so.6
...
```

**What to look for**: `/usr/lib/libchronyctl.so` in the list

**Note**: `pldd` may not be available on all systems.

---

### 9. Using `cat /proc/PID/smaps`

**Description**: Shows detailed memory map with additional information.

**Command**:
```bash
cat /proc/<PID>/smaps | grep -A 20 libchronyctl
```

**Expected Output**:
Shows detailed memory region information including size, permissions, and RSS.

---

## Build System Verification

These methods verify the integration at build time using BitBake/Yocto.

### 10. Check BitBake Task Log

**Description**: Examine the compile and link logs from BitBake.

**Command**:
```bash
# Find the work directory
bitbake -e systimemgr | grep ^WORKDIR=

# Check the log
cat <WORKDIR>/temp/log.do_compile | grep chronyctl
```

**Expected Output**:
```
-lchronyctl
```

**What to look for**: The linker flag `-lchronyctl` in the compile/link commands

---

### 11. Check Recipe Dependencies

**Description**: Verify the recipe has correct dependencies.

**Command**:
```bash
bitbake-getvar -r systimemgr DEPENDS
bitbake-getvar -r systimemgr RDEPENDS
```

**Expected Output**:
```
DEPENDS = "... rdkchronylibctrl ..."
RDEPENDS = "... rdkchronylibctrl ..."
```

---

### 12. Verify Package Installation

**Description**: Check if the library package is actually installed on the image.

**Command**:
```bash
# On the target device
opkg list-installed | grep chronylibctrl

# Or check files
ls -la /usr/lib/libchronyctl.so*
```

**Expected Output**:
```
rdkchronylibctrl - 1.0-r0

# Or
-rwxr-xr-x 1 root root 12345 Jan 1 12:00 /usr/lib/libchronyctl.so
```

---

### 13. Check Build Dependencies Graph

**Description**: Visualize the dependency tree.

**Command**:
```bash
bitbake -g systimemgr
cat task-depends.dot | grep chronylibctrl
```

**What to look for**: Edges showing systimemgr depends on rdkchronylibctrl

---

## Quick Reference

| Method | When | Command | What to Look For |
|--------|------|---------|------------------|
| **ldd** | Build-time/Runtime | `ldd /usr/bin/sysTimeMgr` | `libchronyctl.so` in output |
| **readelf** | Build-time | `readelf -d /usr/bin/sysTimeMgr \| grep NEEDED` | `NEEDED Shared library: [libchronyctl.so]` |
| **objdump** | Build-time | `objdump -p /usr/bin/sysTimeMgr \| grep NEEDED` | `NEEDED libchronyctl.so` |
| **nm** | Build-time | `nm -D /usr/bin/sysTimeMgr \| grep chrony` | Undefined chrony symbols (U) |
| **/proc/maps** | Runtime | `cat /proc/<PID>/maps \| grep chrony` | Memory regions with libchronyctl.so |
| **pmap** | Runtime | `pmap <PID> \| grep chrony` | libchronyctl.so entries |
| **pldd** | Runtime | `pldd <PID>` | /usr/lib/libchronyctl.so in list |
| **BitBake logs** | Build-time | Check `temp/log.do_compile` | `-lchronyctl` flag |
| **File exists** | Runtime | `ls /usr/lib/libchronyctl.so` | File exists |
| **Package installed** | Runtime | `opkg list-installed \| grep chrony` | rdkchronylibctrl package |

---

## Troubleshooting

### Library NOT showing up in ldd/readelf?

**Possible causes**:
1. ❌ Missing from `DEPENDS` in recipe (build won't find headers)
2. ❌ Missing `-lchronyctl` in `LDFLAGS` (linker won't link it)
3. ✅ Check recipe has both `DEPENDS` and `LDFLAGS`

### Library NOT showing up in /proc/PID/maps?

**Possible causes**:
1. ❌ Missing from `RDEPENDS` in recipe (library not installed on target)
2. ❌ Library file not in filesystem
3. ✅ Check recipe has `RDEPENDS` and library file exists

### Binary shows library in ldd but not in /proc/PID/maps?

**Likely cause**:
- Library is linked but functions are never called, so dynamic linker doesn't load it
- Or library is lazy-loaded and hasn't been triggered yet

**Solution**: Use `LD_BIND_NOW=1` to force immediate binding:
```bash
LD_BIND_NOW=1 /usr/bin/sysTimeMgr &
cat /proc/$!/maps | grep chrony
```

---

## Complete Verification Checklist

For **complete** library linkage verification, check ALL three:

- [ ] **Build-time**: `ldd` or `readelf` shows `libchronyctl.so`
- [ ] **Deploy-time**: Library file exists: `ls /usr/lib/libchronyctl.so`  
- [ ] **Runtime**: `/proc/PID/maps` shows `libchronyctl.so` loaded

If any step fails, check the corresponding recipe variable:
- Build-time fail → Check `DEPENDS`
- Deploy-time fail → Check `RDEPENDS`
- Link-time fail → Check `LDFLAGS`

---

## Summary

**Simplest verification method**: On the target device:
```bash
# Method 1: Check if library file exists
ls -la /usr/lib/libchronyctl.so

# Method 2: Check if binary needs it
ldd /usr/bin/sysTimeMgr | grep chrony

# Method 3: Check if process has it loaded
cat /proc/$(pidof sysTimeMgr)/maps | grep chrony
```

**All three should succeed** for proper integration! ✅
