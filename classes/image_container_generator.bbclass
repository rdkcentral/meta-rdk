################################################################################
# If not stated otherwise in this file or this component's Licenses.txt file the
# following copyright and licenses apply:
#
# Copyright 2017 Liberty Global B.V.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
################################################################################

# ------------------------------------------------------------------------------
# Image class responsible for invoking python container generator tool
# responsible for container environment creation.
# ------------------------------------------------------------------------------
do_rootfs[depends] += "lxc-container-generator-native:do_populate_sysroot"

SD_NOTIFY_SLEEP_US ??= ""
TOOL_DIR="${STAGING_DATADIR_NATIVE}/lxc-container-generator"
XML_CONF="${TOOL_DIR}/non_secure"
XML_CONF_SECURE="${TOOL_DIR}/secure"
TOOL="${TOOL_DIR}/src/__main__.py"

ROOTFS_POSTPROCESS_COMMAND:append = "generate_containers_environment ;"

def get_oe_version(bb, d):
     bb_version = d.getVar('BB_VERSION', True) or ""
     if bb_version in ['2.0.0', '2.2.0' ]:
         return "4.0"
     if bb_version in [ '1.44.0', '1.46.0']:
         return "3.1"
     if bb_version in [ '1.30.0' ]:
         return "2.1"
     if bb_version in [ '1.32.0' ]:
         return "2.2"
     if bb_version in [ '1.22.0' ]:
         return "1.6"
     return "UNKNOWN"

def define_soc_version(bb, d):
     arch = d.getVar('TARGET_ARCH', True)
     if arch in [ 'i686' ]:
         return arch
     raw_version = d.getVar('PREFERRED_VERSION_broadcom-refsw', True) or ""
     if raw_version in [ 'unified-16.3-generic-rdk' ]:
         return arch + "_16.3"
     if raw_version in [ 'unified-16.4-generic-rdk' ]:
         return arch + "_16.4"
     if raw_version in [ 'unified-17.1-generic-rdk' ]:
         return arch + "_17.1"
     if raw_version in [ 'unified-18.1-generic-rdk' ]:
         return arch + "_18.1"
     if raw_version in [ 'unified-18.2-generic-rdk' ]:
         return arch + "_18.2"
     if raw_version in [ 'unified-18.3-generic-rdk' ]:
         return arch + "_18.3"
     return "UNKNOWN"

def define_build_version(bb, d):
    if bb.utils.contains('MACHINE_FEATURES', 'debug', True, False, d):
        return "debug"
    elif bb.utils.contains('MACHINE_FEATURES', 'release', True, False, d):
        return "release"
    elif bb.utils.contains('MACHINE_FEATURES', 'production', True, False, d):
        return "production"
    return "debug"

def define_sanity_check(bb, d):
    if bb.utils.contains('MACHINE_FEATURES', 'debug', True, False, d):
        return ""
    elif bb.utils.contains('MACHINE_FEATURES', 'release', True, False, d):
        return ""
    elif bb.utils.contains('MACHINE_FEATURES', 'production', True, False, d):
        return "-S"
    return ""

# ------------------------------------------------------------------------------
# Function responsible for invoking python tool for each XML configuration
# ------------------------------------------------------------------------------
#-> type           -rootfs type (read-only, read-write)
#-> soc_ver        -define arch and broadcom-refsw version
#-> oe_ver         -yocto/open-embedded version
#-> shared_rootfs  -disable mount namespace, containers and host shared rootfs
#-> version        -build version: debug, release, production
# ------------------------------------------------------------------------------
generate_containers_environment() {

        secure='${@bb.utils.contains("SECURE_CONTAINERS", "1", "-s", "",d)}'
        soc_ver="${@define_soc_version(bb, d)}"
        oe_ver="${@get_oe_version(bb, d)}"
        # set shared_rootfs to -e to enable shared rootfs between containers and host
        shared_rootfs=""
        version="${@define_build_version(bb, d)}"
        # example rdk and rdkhal layer versions, in real life they would come from env variables set by build config
        rdk_ver="1"
        rdkhal_ver="1"
        sanityCheck="${@define_sanity_check(bb, d)}"
        sleep_us='${@oe.utils.conditional("SD_NOTIFY_SLEEP_US", "", "", "--usleep=${SD_NOTIFY_SLEEP_US}",d)}'
        common_options="$sleep_us $sanityCheck -t SOC_VER=$soc_ver,OE_VER=$oe_ver,version=$version,RDK_VER=$rdk_ver,RDKHAL_VER=$rdkhal_ver"

# NON SECURE CONTAINERS
        files="`find ${XML_CONF} -type f -a -name '*.xml'|sort -V`"
        if [ "$files" != "" ]; then
                python ${TOOL} -r ${IMAGE_ROOTFS} $secure $shared_rootfs $common_options $files
        fi

# SECURE CONTAINERS
        files="`find ${XML_CONF_SECURE} -type f -a -name '*.xml' -a \! -name '*_DBUS*'|sort -V`"
        if [ "$files" != "" ]; then
                python ${TOOL} -r ${IMAGE_ROOTFS} -s $shared_rootfs $common_options $files
        fi
        # DBUS does not start properly in container with a shared rootfs, so overrule this option here
        files="`find ${XML_CONF_SECURE} -type f -a -name '*.xml' -a -name '*_DBUS*'|sort -V`"
        if [ "$files" != "" ]; then
                python ${TOOL} -r ${IMAGE_ROOTFS} -s                $common_options $files
        fi

}

