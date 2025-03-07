SUMMARY = "Sys mon tool - SYS RESOURCE recipe"

DESCRIPTION = "Sys mon tool - SYS RESOURCE recipe"

SECTION = "console/utils"

include sys_mon_tools.inc

SYSMONTOOL_NAME = "sys_resource"


export USE_SYS_RESOURCE_MLT="y"
export USE_IDLE_MLT="y"
export USE_MPEMEDIATUNE_MLT="n"
export USE_SYS_RESOURCE_MON="y"
export USE_SYSRES_PLATFORM="LINUX"

PACKAGECONFIG = "sys-resource-mlt analyze-idle-mlt mlt-bt"
PACKAGECONFIG[sys-resource-mlt] = "--enable-sys-resource-mlt,,,"
PACKAGECONFIG[analyze-idle-mlt] = "--enable-analyze-idle-mlt,,,"
PACKAGECONFIG[lnk-mlt] = "--enable-lnk-mlt,,,"
PACKAGECONFIG[file-wdr] = "--enable-file-wdr,,,"
PACKAGECONFIG[cmas-cp] = "--enable-cmas-cp,,,"
PACKAGECONFIG[cmas-it] = "--enable-cmas-it,,,"
PACKAGECONFIG[mlt-bt] = "--enable-mlt-bt,,,"
PACKAGECONFIG[cmat-sstats] = "--enable-cmat-sstats,,,"

CFLAGS:append:broadband = " -DRDK_BROADBAND"
CXXFLAGS:append:broadband = " -DRDK_BROADBAND"


inherit autotools pkgconfig coverity
