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

include add-users-groups-file-owners-and-permissions.inc

# Use the oe-core version of "setcap" instead of relying on the host version
do_rootfs[depends] += "libcap-native:do_populate_sysroot"

ROOTFS_POSTPROCESS_COMMAND:append = "chown_chmod_secap; "

python chown_chmod_secap() {
    import getopt
    for line in d.getVar("ROOTFS_CHOWN_SETCAP", True).replace("\\n", "\n").split("\n"):
        line = line.strip()
        if line:
            opts, files = getopt.getopt(line.split(), "tdo:m:c:")
            doTouch = False
            doMkdir = False
            doChown = None
            doChmod = None
            doSetcap = None
            if not files:
                bb.error("ROOTFS_CHOWN_SETCAP: invalid line, missing file(s): %s" % line)
            for o, a in opts:
                if o == "-d":
                    doMkdir = True
                elif o == "-t":
                    doTouch = True
                elif o == "-o":
                    doChown = a
                elif o == "-m":
                    doChmod = a
                elif o == "-c":
                    doSetcap = a
                else:
                    bb.error("ROOTFS_CHOWN_SETCAP: invalid line, unknown option %s: %s" % (o, line))

            files = [d.getVar("IMAGE_ROOTFS", True) + f for f in files]
            from os.path import exists as file_exists
            from subprocess import check_call
            for f in files:
                if doMkdir:
                    check_call(["mkdir", "-p"] + [f])
                if doTouch:
                    check_call(["touch"] + [f])
                if file_exists(f):
                    if doChown:
                        check_call(["chown", "-R", doChown] + [f])
                    if doChmod:
                        check_call(["chmod", "-R", doChmod] + [f])
                    if doSetcap:
                        check_call(["setcap", doSetcap] + [f])
}
