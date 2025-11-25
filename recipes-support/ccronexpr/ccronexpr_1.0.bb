SUMMARY = "ccronexpr library"
SECTION = "libs"
DESCRIPTION = "Library for ccronexpr"
HOMEPAGE = "https://github.com/staticlibs/ccronexpr"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=646c743a776a3dc373f94d63fb2f1a71"

SRC_URI = "git://github.com/staticlibs/ccronexpr.git;branch=master"
SRCREV = "17475e10c2053650a86b1d25a692c2c20d74c420"

SRC_URI += "file://0001-ccronexpr-cmakeLists.patch"
SRC_URI += "file://0002-Add-CRON_USE_LOCAL_TIME-preprocessor-option.patch"

S = "${WORKDIR}/git"

inherit cmake

TARGET_CFLAGS += "-DCRON_USE_LOCAL_TIME"

