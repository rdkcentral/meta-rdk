# generating minidumps symbols
inherit breakpad
DEPENDS += "breakpad breakpad-wrapper"
BREAKPAD_BIN:append = "rfc"

LDFLAGS += "-lbreakpadwrapper -lpthread -lstdc++"
CFLAGS += "-DINCLUDE_BREAKPAD"
# generating minidumps
PACKAGECONFIG:append = " breakpad"
