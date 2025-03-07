#!/bin/sh
##########################################################################
# If not stated otherwise in this file or this component's LICENSE
# file the following copyright and licenses apply:
#
# Copyright 2020 RDK Management
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
##########################################################################

ROOT_PREFIX=""
[ -n "$1" ] && ROOT_PREFIX="$1"
ROOT_PATH="${ROOT_PREFIX}/"
BIN_PATH="usr/bin/"
BIN_NAME="hello"

HELLO_BIN="${ROOT_PATH}${BIN_PATH}${BIN_NAME}"

if [ -x "${HELLO_BIN}" ]; then
	echo "Executing ${HELLO_BIN} binary ${VERSION}"
	${HELLO_BIN}
	exit $?
else
	echo "${HELLO_BIN} is not found or is not executable"
	exit 1
fi
