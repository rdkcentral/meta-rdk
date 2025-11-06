# ASLR enabled
# we set this to "y" or "n" and use it as a conditional later

ASLR = "${@bb.utils.contains('DISTRO_FEATURES', 'aslr', 'y', 'n', d)}"

# toggle aslr off for specific packages
ASLR:pn-ccsp-wifi-agent = "n"
ASLR:pn-docsis = "n"
# ASLR:pn-hal-wifi = "n"
ASLR:pn-mesh-agent = "n"
ASLR:pn-tr69hostif = "n"
ASLR:pn-qtbase = "n"
ASLR:pn-ccsp-xconf = "n"
ASLR:pn-qtn-csmd-logic = "n"
ASLR:pn-plumewifi = "n"
ASLR:pn-hal-mta-broadcom = "n"
ASLR:pn-broadcom-refsw = "n"
ASLR:pn-broadcom-ursr = "n"
ASLR:pn-ccsp-xdns = "n"

# Warn on use of format strings that represent possible security problems
SECURITY_STRINGFORMAT ?= "-Wformat -Wformat-security -Wno-error=format-security"

# packages which explicitly set a "-Wno-format" preventing the -Wformat-security checks
# (we remove -Wformat-security or the compiler crashes on the conflict with -Wno-format)

SECURITY_STRINGFORMAT:pn-audiohal = ""
SECURITY_STRINGFORMAT:pn-gptimer = ""
SECURITY_STRINGFORMAT:pn-pcd = ""
SECURITY_STRINGFORMAT:pn-qtn-qwcfg = ""

# ASLR support
# --enable-default-pie   compile as position independent executable (gcc-6.x)
# -no-pie                cancel the effects of --enable-default-pie (gcc-6.x)
#
# Additional compile flags
# _FORTIFY_SOURCE        perform additional compile time checks
# -fstack-protector      protect stack against buffer overruns
# -Wl,-z,relro,-z,now    prevent changes to relocation entries

SECURITY_NOPIE ?= "${@ASLR == "y" and GCCVERSION > "6.0" and "-no-pie -fno-PIE" or ""}"
SECURITY_FORTIFY ?= "-D_FORTIFY_SOURCE=2 -Wno-error=unused-result"
SECURITY_CFLAGS ?= "-fstack-protector ${SECURITY_FORTIFY} ${SECURITY_STRINGFORMAT}"
SECURITY_NO_PIE_CFLAGS ?= "-fstack-protector ${SECURITY_FORTIFY} ${SECURITY_STRINGFORMAT} ${SECURITY_NOPIE}"
SECURITY_LDFLAGS ?= "-fstack-protector -Wl,-z,relro"
# removed this from SECURITY_LDFLAGS ",-z,now"

# packages which break due to the extra compiler checks enabled by _FORTIFY
# most of these are caused by calls to open() with O_CREAT that are mising file permissions
# (TODO: patch compile issues and remove these lines)

SECURITY_FORTIFY:pn-alljoyn = ""
SECURITY_FORTIFY:pn-audiohal = ""
SECURITY_FORTIFY:pn-avcap-samples = ""
SECURITY_FORTIFY:pn-bluetooth-core = ""
SECURITY_FORTIFY:pn-ccsp-tr069-pa = ""
SECURITY_FORTIFY:pn-ccsp-p-and-m = ""
SECURITY_FORTIFY:pn-ccsp-snmp-pa = ""
SECURITY_FORTIFY:pn-comcast-firmwaretool = ""
SECURITY_FORTIFY:pn-ctrlm-testapp = ""
SECURITY_FORTIFY:pn-dial = ""
SECURITY_FORTIFY:pn-docsis = ""
SECURITY_FORTIFY:pn-ebtables = ""
SECURITY_FORTIFY:pn-ecfs-search = ""
SECURITY_FORTIFY:pn-extswitch = ""
SECURITY_FORTIFY:pn-factory-nvram = ""
SECURITY_FORTIFY:pn-gdb = ""
SECURITY_FORTIFY:pn-hal-cm-xb6 = ""
SECURITY_FORTIFY:pn-keyutils = ""
SECURITY_FORTIFY:pn-liblan-service = ""
SECURITY_FORTIFY:pn-libpcd = ""
SECURITY_FORTIFY:pn-libunwind = ""
SECURITY_FORTIFY:pn-logger = ""
SECURITY_FORTIFY:pn-mini-cli = ""
SECURITY_FORTIFY:pn-ncurses = ""
SECURITY_FORTIFY:pn-otamgr = ""
SECURITY_FORTIFY:pn-pcd = ""
SECURITY_FORTIFY:pn-production-data = ""
SECURITY_FORTIFY:pn-qca = ""
SECURITY_FORTIFY:pn-qtn-qwcfg = ""
SECURITY_FORTIFY:pn-qtn-qevent = ""
SECURITY_FORTIFY:pn-ucresolv = ""
SECURITY_FORTIFY:pn-upgradebox = ""
SECURITY_FORTIFY:pn-utopia = ""
SECURITY_FORTIFY:pn-xrtestapp = ""
SECURITY_FORTIFY:pn-broadcom-mocadriver = ""


# workarounds for various packages
SECURITY_CFLAGS:append:pn-broadcom-refsw = " ${SECURITY_NOPIE} ${SECURITY_LDFLAGS}"
SECURITY_CFLAGS:append:pn-broadcom-ursr = " ${SECURITY_NOPIE} ${SECURITY_LDFLAGS}"
SECURITY_CFLAGS:append:pn-ctrlm-main = " ${SECURITY_NOPIE} ${SECURITY_LDFLAGS}"

# don't set eglibc compile flags
SECURITY_CFLAGS:pn-eglibc = ""
SECURITY_CFLAGS:pn-eglibc-initial = ""
SECURITY_LDFLAGS:remove:pn-eglibc = "-fstack-protector"
SECURITY_LDFLAGS:remove:pn-eglibc-initial = "-fstack-protector"

# don't set glibc compile flags
SECURITY_CFLAGS:pn-glibc = ""
SECURITY_CFLAGS:pn-glibc-initial = ""
SECURITY_LDFLAGS:remove:pn-glibc = "-fstack-protector"
SECURITY_LDFLAGS:remove:pn-glibc-initial = "-fstack-protector"

SECURITY_CFLAGS:pn-gcc-runtime = ""
SECURITY_LDFLAGS:remove:pn-gcc-runtime = "-fstack-protector"

# kernel modules can't be -pie
TARGET_CC_KERNEL_ARCH:append = " ${SECURITY_NOPIE}"
MAKE:append:pn-broadcom-refsw = " KCFLAGS=-fno-PIE"
MAKE:append:pn-broadcom-ursr = " KCFLAGS=-fno-PIE"
MOD_OPTS:append:pn-broadcom-rgdriver = " KCFLAGS=-fno-PIE"

TARGET_CC_ARCH:append:class-target = " ${@ASLR == "y" and SECURITY_CFLAGS or ""}"
TARGET_LDFLAGS:append:class-target = " ${@ASLR == "y" and SECURITY_LDFLAGS or ""}"

include conf/distro/include/rdk_security_flags-cpc.inc
