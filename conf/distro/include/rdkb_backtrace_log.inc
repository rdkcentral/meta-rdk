# backtrace logging is enabled
# we set this to "y" or "n" and use it as a conditional later

LOG_BACKTRACE = "${@bb.utils.contains('DISTRO_FEATURES', 'logbacktrace', 'y', 'n', d)}"

BACKTRACE_LDFLAGS ?= ""
BACKTRACE_DEPENDS ?= ""


BACKTRACE_DEPENDS:pn-ccsp-gwprovapp = " breakpad-wrapper"
BACKTRACE_DEPENDS:pn-utopia = " breakpad-wrapper"
BACKTRACE_DEPENDS:pn-parodus = " breakpad-wrapper"
BACKTRACE_DEPENDS:pn-mqttcm = " breakpad-wrapper"

BACKTRACE_LDFLAGS:pn-ccsp-gwprovapp = " -lbreakpadwrapper "
BACKTRACE_LDFLAGS:pn-utopia = " -Wl,--no-as-needed -lbreakpadwrapper -Wl,--as-needed "
BACKTRACE_LDFLAGS:pn-parodus = " -Wl,--no-as-needed -lbreakpadwrapper -Wl,--as-needed "
BACKTRACE_LDFLAGS:pn-mqttcm = " -Wl,--no-as-needed -lbreakpadwrapper -Wl,--as-needed "

DEPENDS:append = " ${@LOG_BACKTRACE == "y" and BACKTRACE_DEPENDS or ""}"
LDFLAGS:append = " ${@LOG_BACKTRACE == "y" and BACKTRACE_LDFLAGS or ""}"
