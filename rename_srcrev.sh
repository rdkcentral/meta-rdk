#!/bin/sh
##########################################################################
# If not stated otherwise in this file or this component's Licenses.txt
# file the following copyright and licenses apply:
#
# Copyright 2021 RDK Management
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
if [ -f ${PWD}/../versions.conf ]; then
        sed -i '/meta-/d' ${PWD}/../versions.conf
        sed -i '/hooks/d' ${PWD}/../versions.conf
        sed -i '/openembedded/d' ${PWD}/../versions.conf
        sed -i '/bitbake/d' ${PWD}/../versions.conf
        sed -z -i 's+SRCREV:pn-rdk/components/generic/closedcaption/generic+SRCREV:pn-closedcaption-hal-headers+' ${PWD}/../versions.conf
        sed -z -i 's+SRCREV:pn-rdk/components/generic/closedcaption/generic+SRCREV:pn-closedcaption+' ${PWD}/../versions.conf
        sed -z -i 's+SRCREV:pn-rdk/components/generic/ledmgr/generic+SRCREV:pn-ledmgr+' ${PWD}/../versions.conf
        sed -z -i 's+SRCREV:pn-rdk/components/generic/ledmgr/generic+SRCREV:pn-ledmgr-headers+' ${PWD}/../versions.conf
        sed -z -i 's+SRCREV:pn-rdk/components/generic/ledmgr/generic+SRCREV:pn-ledmgr-extended-noop+' ${PWD}/../versions.conf
        sed -z -i 's+SRCREV:pn-rdk/components/generic/devicesettings/generic+SRCREV:pn-devicesettings+' ${PWD}/../versions.conf
        sed -z -i 's+SRCREV:pn-rdk/components/generic/devicesettings/generic+SRCREV:pn-devicesettings-hal-headers+g' ${PWD}/../versions.conf
        sed -z -i 's+SRCREV:pn-rdk/components/generic/hdmicec/generic+SRCREV:pn-hdmicec+' ${PWD}/../versions.conf
        sed -z -i 's+SRCREV:pn-rdk/components/generic/hdmicec/generic+SRCREV:pn-hdmicecheader+' ${PWD}/../versions.conf
        sed -z -i 's+SRCREV:pn-rdk/components/generic/iarmmgrs/generic+SRCREV:pn-iarmmgrs+' ${PWD}/../versions.conf
        sed -z -i 's+SRCREV:pn-rdk/components/generic/iarmmgrs/generic+SRCREV:pn-iarmmgrs-hal-headers+' ${PWD}/../versions.conf
        sed -z -i 's+SRCREV:pn-rdk/components/generic/media_utils/generic+SRCREV:pn-media-utils+' ${PWD}/../versions.conf
        sed -z -i 's+SRCREV:pn-rdk/components/generic/media_utils/generic+SRCREV:pn-media-utils-headers+' ${PWD}/../versions.conf
        sed -z -i 's+SRCREV:pn-rdk/components/thirdparty/dimark/tr69-4.4/devices/raspberrypi+SRCREV:pn-tr69hostif+' ${PWD}/../versions.conf
        sed -z -i 's+SRCREV:pn-rdk/components/thirdparty/dimark/tr69-4.4/devices/raspberrypi+SRCREV:pn-tr69hostif-headers+' ${PWD}/../versions.conf
        sed -z -i 's+SRCREV:pn-rdk/components/generic/audioserver/generic+SRCREV:pn-audioserver+' ${PWD}/../versions.conf
        sed -z -i 's+SRCREV:pn-rdk/components/generic/audioserver/generic+SRCREV:pn-audioserver-headers+' ${PWD}/../versions.conf
        sed -z -i 's+SRCREV:pn-rdk/components/generic/audioserver/generic+SRCREV:pn-audioserver-sample-apps+' ${PWD}/../versions.conf
        sed -z -i 's+SRCREV:pn-rdk/components/generic/rbuscore/generic+SRCREV:pn-rbus-core+' ${PWD}/../versions.conf
        sed -z -i 's+SRCREV:pn-rdk/components/opensource/rtmessage+SRCREV:pn-rtmessage+' ${PWD}/../versions.conf
        sed -z -i 's+SRCREV:pn-rdk/components/generic/tdk/generic+SRCREV:pn-tdk+' ${PWD}/../versions.conf
        sed -z -i 's+SRCREV:pn-rdk/components/generic/tdk/generic+SRCREV:pn-installtdk+' ${PWD}/../versions.conf
        sed -z -i 's+SRCREV:pn-rdk/components/generic/mediaframework/generic+SRCREV:pn-rmfgeneric+' ${PWD}/../versions.conf
        sed -z -i 's+SRCREV:pn-rdk/components/generic/mediaframework/generic+SRCREV:pn-rmfapp+' ${PWD}/../versions.conf
        sed -z -i 's+SRCREV:pn-rdk/components/generic/mediaframework/generic+SRCREV:pn-rmfgenericheader+' ${PWD}/../versions.conf
        sed -z -i 's+SRCREV:pn-rdk/components/generic/mediaframework/generic+SRCREV:pn-rmfgenericheaders+' ${PWD}/../versions.conf
        sed -z -i 's+SRCREV:pn-rdk/components/generic/mediaframework/generic+SRCREV:pn-trh+' ${PWD}/../versions.conf
        sed -z -i 's+SRCREV:pn-rdk/components/generic/mediaframework/generic+SRCREV:pn-rtrmfplayer+' ${PWD}/../versions.conf
        sed -z -i 's+SRCREV:pn-rdk/components/generic/mediaframework/generic+SRCREV:pn-rmfosal+' ${PWD}/../versions.conf
        sed -z -i 's+SRCREV:pn-rdk/components/generic/mediaframework/generic+SRCREV:pn-sectionfilter-hal-stub+' ${PWD}/../versions.conf
        sed -z -i 's+SRCREV:pn-rdk/components/generic/mediaframework/generic+SRCREV:pn-runsnmp-emu+' ${PWD}/../versions.conf
	sed -z -i 's+SRCREV:pn-rdk/components/generic/mediaframework/generic+SRCREV:pn-runsnmp+' ${PWD}/../versions.conf
        sed -z -i 's+SRCREV:pn-rdk/components/generic/mediaframework/generic+SRCREV:pn-rmfpodserver+' ${PWD}/../versions.conf
        sed -z -i 's+SRCREV:pn-rdk/components/generic/mediaframework/generic+SRCREV:pn-runpod+' ${PWD}/../versions.conf
        sed -z -i 's+SRCREV:pn-rdk/components/generic/mediaframework/generic+SRCREV:pn-rmfpodmgrheaders+' ${PWD}/../versions.conf
        sed -z -i 's+SRCREV:pn-rdk/components/generic/mediaframework/generic+SRCREV:pn-rmfpodmgr+' ${PWD}/../versions.conf
        sed -z -i 's+SRCREV:pn-rdk/components/generic/mediaframework/generic+SRCREV:pn-rmfhalheaders+' ${PWD}/../versions.conf
        sed -z -i 's+SRCREV:pn-rdk/components/generic/dvr/generic+SRCREV:pn-dvrgenericheaders+' ${PWD}/../versions.conf
        sed -z -i 's+SRCREV:pn-rdk/components/generic/dvr/generic+SRCREV:pn-dvrmgr+' ${PWD}/../versions.conf
        sed -z -i 's+SRCREV:pn-rdk/components/generic/appmanager/generic+SRCREV:pn-appmanager+' ${PWD}/../versions.conf
        sed -z -i 's+SRCREV:pn-rdk/components/generic/appmanager/generic+SRCREV:pn-rdkresidentapp+' ${PWD}/../versions.conf
        sed -z -i 's+SRCREV:pn-rdk/components/opensource/qt5_1/generic+SRCREV:pn-qtbase+' ${PWD}/../versions.conf
        sed -z -i 's+SRCREV:pn-rdk/components/opensource/qt5_1/generic+SRCREV:pn-qtbase-native+' ${PWD}/../versions.conf
        sed -z -i 's+SRCREV:pn-rdk/components/generic/wifi/generic+SRCREV:pn-wifi-hal-generic+' ${PWD}/../versions.conf
        sed -z -i 's+SRCREV:pn-rdk/components/generic/wifi/generic+SRCREV:pn-wifi-hal-headers+' ${PWD}/../versions.conf
        sed -z -i 's+SRCREV:pn-rdk/components/generic/xraudio/hal_ctrlm+SRCREV:pn-ctrlm-xraudio-hal+' ${PWD}/../versions.conf
        sed -z -i 's+SRCREV:pn-rdk/components/generic/xraudio/hal_ctrlm+SRCREV:pn-ctrlm-xraudio-hal-headers+' ${PWD}/../versions.conf
	sed -z -i 's+SRCREV:pn-rdk/components/generic/systemtimemgr/generic+SRCREV:pn-systimemgrinetrface+' ${PWD}/../versions.conf
	sed -z -i 's+SRCREV:pn-rdk/components/generic/systemtimemgr/generic+SRCREV:pn-systimemgrfactory+' ${PWD}/../versions.conf
	sed -z -i 's+SRCREV:pn-rdk/components/generic/systemtimemgr/generic+SRCREV:pn-systimemgr+' ${PWD}/../versions.conf
        sed -z -i 's+SRCREV:pn-components/opensource/RDK_apps+SRCREV:pn-residentui+' ${PWD}/../versions.conf
        sed -z -i 's+SRCREV:pn-components/opensource/RDK_apps+SRCREV:pn-lxapp-diag+' ${PWD}/../versions.conf
        sed -z -i 's+SRCREV:pn-components/opensource/RDK_apps+SRCREV:pn-lxapp-bt-audio+' ${PWD}/../versions.conf
        sed -z -i 's+SRCREV:pn-rdk/components/generic/mediaframework/devices/intel-x86-pc/rdkri+SRCREV:pn-sectionfilter-hal-noop+' ${PWD}/../versions.conf
        sed -z -i 's+SRCREV:pn-rdk/components/generic/mediaframework/devices/intel-x86-pc/rdkri+SRCREV:pn-dvrmgr-hal-noop+' ${PWD}/../versions.conf
	sed -z -i 's+SRCREV:pn-rdk/components/thirdparty/cablelabs/chila/podManager/generic+SRCREV:pn-rmfpodmgrheaders-priv+' ${PWD}/../versions.conf
	sed -z -i 's+SRCREV:pn-rdk/components/thirdparty/cablelabs/chila/podManager/generic+SRCREV:pn-snmpmanager-priv+' ${PWD}/../versions.conf
	sed -z -i 's+SRCREV:pn-rdk/components/thirdparty/cablelabs/chila/podManager/generic+SRCREV:pn-podserver-priv+' ${PWD}/../versions.conf
	sed -z -i 's+SRCREV:pn-rdk/components/generic/tdk/tdk-advanced/generic+SRCREV:pn-tdksm+' ${PWD}/../versions.conf
	sed -z -i 's+SRCREV:pn-rdk/components/generic/tdk/tdk-advanced/generic+SRCREV:pn-tdkadvanced+' ${PWD}/../versions.conf
	sed -z -i 's+SRCREV:pn-rdk/components/cpc/OCDM-Widevine/generic+SRCREV:pn-wpeframework-ocdm-widevine+' ${PWD}/../versions.conf
	sed -z -i 's+SRCREV:pn-rdk/components/cpc/OCDM-Widevine/generic+SRCREV:pn-wpeframework-ocdm-widevine2+' ${PWD}/../versions.conf
	sed -z -i 's+SRCREV:pn-rdk/components/cpc/rf4ce/generic+SRCREV:pn-rf4ce+' ${PWD}/../versions.conf
	sed -z -i 's+SRCREV:pn-rdk/components/cpc/rf4ce/generic+SRCREV:pn-rf4ce-headers+' ${PWD}/../versions.conf
	sed -z -i 's+SRCREV:pn-rdk/components/cpc/vod-client/generic+SRCREV:pn-vodclient-java+' ${PWD}/../versions.conf
	sed -z -i 's+SRCREV:pn-rdk/components/cpc/vod-client/generic+SRCREV:pn-vodclient-app+' ${PWD}/../versions.conf
	sed -z -i 's+SRCREV:pn-rdk/components/cpc/lxy/generic+SRCREV:pn-lxy+' ${PWD}/../versions.conf
	sed -z -i 's+SRCREV:pn-rdk/components/cpc/lxy/generic+SRCREV:pn-lxyupdate+' ${PWD}/../versions.conf
	sed -z -i 's+SRCREV:pn-rdk/components/cpc/xreplugins/generic+SRCREV:pn-netflix-plugin+' ${PWD}/../versions.conf
	sed -z -i 's+SRCREV:pn-rdk/components/cpc/xreplugins/generic+SRCREV:pn-wayland-plugin-default+' ${PWD}/../versions.conf
	sed -z -i 's+SRCREV:pn-rdk/components/cpc/xreplugins/generic+SRCREV:pn-xre2-plugin-default+' ${PWD}/../versions.conf
	sed -z -i 's+SRCREV:pn-rdk/components/cpc/ave/generic+SRCREV:pn-ave-crypto+' ${PWD}/../versions.conf
	sed -z -i 's+SRCREV:pn-rdk/components/cpc/ave/generic+SRCREV:pn-ave-filesystem+' ${PWD}/../versions.conf
	sed -z -i 's+SRCREV:pn-rdk/components/cpc/ave/generic+SRCREV:pn-ave-trace+' ${PWD}/../versions.conf
	sed -z -i 's+SRCREV:pn-rdk/components/cpc/ave/generic+SRCREV:pn-ave-ve+' ${PWD}/../versions.conf
	sed -z -i 's+SRCREV:pn-rdk/components/cpc/ave/generic+SRCREV:pn-ave-auditude+' ${PWD}/../versions.conf
	sed -z -i 's+SRCREV:pn-rdk/components/cpc/ave/generic+SRCREV:pn-ave-safe-apis+' ${PWD}/../versions.conf
	sed -z -i 's+SRCREV:pn-rdk/components/cpc/ave/generic+SRCREV:pn-ave-drm+' ${PWD}/../versions.conf
	sed -z -i 's+SRCREV:pn-rdk/components/cpc/ave/generic+SRCREV:pn-ave-psdk+' ${PWD}/../versions.conf
	sed -z -i 's+SRCREV:pn-rdk/components/cpc/ave/generic+SRCREV:pn-ave-psdkutils+' ${PWD}/../versions.conf
	sed -z -i 's+SRCREV:pn-rdk/components/cpc/ave/generic+SRCREV:pn-ave-adapter+' ${PWD}/../versions.conf
	sed -z -i 's+SRCREV:pn-rdk/components/cpc/ave/generic+SRCREV:pn-ave-kernel+' ${PWD}/../versions.conf
	sed -z -i 's+SRCREV:pn-rdk/components/cpc/ave/generic+SRCREV:pn-ave-text+' ${PWD}/../versions.conf
	sed -z -i 's+SRCREV:pn-rdk/components/cpc/ave/generic+SRCREV:pn-ave-xmlreader+' ${PWD}/../versions.conf
	sed -z -i 's+SRCREV:pn-rdk/components/cpc/ave/generic+SRCREV:pn-ave-cts+' ${PWD}/../versions.conf
	sed -z -i 's+SRCREV:pn-rdk/components/cpc/ave/generic+SRCREV:pn-ave-net+' ${PWD}/../versions.conf
	sed -z -i 's+SRCREV:pn-rdk/components/cpc/sec-client-rdk/generic+SRCREV:pn-secclient+' ${PWD}/../versions.conf
	sed -z -i 's+SRCREV:pn-rdk/components/cpc/sec-client-rdk/generic+SRCREV:pn-socprovapi-crypto+' ${PWD}/../versions.conf
	sed -z -i 's+SRCREV:pn-rdk/components/cpc/sec-client-rdk/generic+SRCREV:pn-socprovapi+' ${PWD}/../versions.conf
        sed -z -i 's+SRCREV:pn-rdk/components/cpc/sec-client-rdk/generic+SRCREV:pn-secclient+' ${PWD}/../versions.conf
	sed -z -i 's+SRCREV:pn-rdk/components/cpc/rdk-ca-store/generic+SRCREV:pn-rdk-ca-store+' ${PWD}/../versions.conf
	sed -z -i 's+SRCREV:pn-rdk/components/cpc/rdk-ca-store/generic+SRCREV:pn-caupdate+' ${PWD}/../versions.conf
        sed -z -i 's+SRCREV:pn-rdk/components/cpc/sec-apis/generic+SRCREV:pn-secapi-common-crypto+' ${PWD}/../versions.conf
        sed -z -i 's+SRCREV:pn-rdk/components/cpc/sec-apis/generic+SRCREV:pn-secapi-common-hw+' ${PWD}/../versions.conf
	sed -z -i 's+SRCREV:pn-rdk/components/cpc/sec-apis/generic+SRCREV:pn-secapi-common+' ${PWD}/../versions.conf
	sed -z -i 's+SRCREV:pn-rdk/components/generic/sys_mon_tools/key_simulator/generic+SRCREV:pn-key-simulator+' ${PWD}/../versions.conf
        sed -z -i 's+SRCREV:pn-rdk/components/generic/sys_mon_tools/key_simulator/generic+SRCREV:pn-sys-resource+' ${PWD}/../versions.conf
        sed -i 's+SRCREV:pn-rdk/components/generic/gst-plugins-rdk/soc/raspberry/rpi3/playersinkbin+SRCREV:pn-gst-plugins-playersinkbin-raspberry+g' ${PWD}/../versions.conf
        sed -i 's+SRCREV:pn-rdk/components/generic/podmgr/devices/intel-x86-pc/rdkri+SRCREV:pn-podmgr+g' ${PWD}/../versions.conf
        sed -i 's+SRCREV:pn-rdk/components/generic/recorder/generic+SRCREV:pn-recorder+g' ${PWD}/../versions.conf
        sed -i 's+SRCREV:pn-rdk/components/generic/gst-plugins-rdk-dvr/generic+SRCREV:pn-gst-plugins-rdk-dvr+g' ${PWD}/../versions.conf
        sed -i 's+SRCREV:pn-rdk/components/generic/xrpSMEngine/generic+SRCREV:pn-xr-sm-engine+g' ${PWD}/../versions.conf
        sed -i 's+SRCREV:pn-rdk/components/generic/libunpriv/generic+SRCREV:pn-libunpriv+g' ${PWD}/../versions.conf
        sed -i 's+SRCREV:pn-rdk/components/generic/sys_mon_tools/mem_analyser_tools/generic+SRCREV:pn-memstress+g' ${PWD}/../versions.conf
        sed -i 's+SRCREV:pn-rdk/components/generic/telemetry/generic+SRCREV:pn-telemetry+g' ${PWD}/../versions.conf
        sed -i 's+SRCREV:pn-rdk/components/generic/rtcav/generic+SRCREV:pn-rtcav+g' ${PWD}/../versions.conf
        sed -i 's+SRCREV:pn-Comcast/littlesheens.git+SRCREV:pn-littlesheens+g' ${PWD}/../versions.conf
        sed -i 's+SRCREV:pn-rdk/components/opensource/qt5_1/soc/broadcom/common+SRCREV_soc:pn-qtbase+g' ${PWD}/../versions.conf
        sed -i 's+SRCREV:pn-rdk/components/generic/storagemanager/generic+SRCREV:pn-storagemanager+g' ${PWD}/../versions.conf
        sed -i 's+SRCREV:pn-rdk/components/generic/sys_mon_tools/sys_resource/generic+SRCREV:pn-sys-resource +g' ${PWD}/../versions.conf
        sed -i 's+SRCREV:pn-rdk/components/generic/closedcaption/soc/noop/common+SRCREV:pn-closedcaption-hal-noop+g' ${PWD}/../versions.conf
        sed -i 's+SRCREV:pn-rdk/components/generic/rfc/generic+SRCREV:pn-rfc+g' ${PWD}/../versions.conf
        sed -i 's+SRCREV:pn-rdk/components/generic/control/generic+SRCREV:pn-ctrlm-headers+g' ${PWD}/../versions.conf
        sed -i 's+SRCREV:pn-libtom/libtomcrypt.git+SRCREV:pn-libtomcrypt+g' ${PWD}/../versions.conf
        sed -i 's+SRCREV:pn-rdk/components/generic/sys_mon_tools/power-state-monitor/generic+SRCREV:pn-power-state-monitor+g' ${PWD}/../versions.conf
        sed -i 's+SRCREV:pn-rdk/devices/raspberrypi/devicesettings+SRCREV:pn-devicesettings-hal-raspberrypi+g' ${PWD}/../versions.conf
        sed -i 's+SRCREV:pn-rdk/components/generic/ttsengine/generic+SRCREV:pn-tts+g' ${PWD}/../versions.conf
        sed -i 's+SRCREV:pn-rdk/components/opensource/qtwebsockets/generic+SRCREV:pn-qtwebsockets+g' ${PWD}/../versions.conf
        sed -i 's+SRCREV:pn-rdk/yocto_oe/layers/iarmmgrs-hal-sample+SRCREV:pn-iarmmgrs-hal-noop+g' ${PWD}/../versions.conf
        sed -i 's+SRCREV:pn-rdk/components/generic/rmf_mediastreamer/generic+SRCREV:pn-rmfstreamer+g' ${PWD}/../versions.conf
        sed -i 's+SRCREV:pn-rdk/components/generic/crashupload/generic+SRCREV:pn-crashupload+g' ${PWD}/../versions.conf
        sed -i 's+SRCREV:pn-rdk/components/generic/xupnp/generic+SRCREV:pn-xupnp+g' ${PWD}/../versions.conf
        sed -i 's+SRCREV:pn-rdk/components/generic/dtcp/generic+SRCREV:pn-dtcpmgr-noop+g' ${PWD}/../versions.conf
        sed -i 's+SRCREV:pn-rdk/components/generic/sys_mon_tools/sys_utils/generic/+SRCREV:pn-sys-utils+g' ${PWD}/../versions.conf
        sed -i 's+SRCREV:pn-rdk/components/generic/mediaframework/soc/raspberrypi/common+SRCREV:pn-rmfhalheaders+g' ${PWD}/../versions.conf
        sed -i 's+SRCREV:pn-rdk/components/generic/sysint/devices/raspberrypi+SRCREV_soc:pn-sysint+g' ${PWD}/../versions.conf
        sed -i 's+SRCREV:pn-rdk/components/generic/hwselftest/generic+SRCREV:pn-hwselftest+g' ${PWD}/../versions.conf
        sed -i 's+SRCREV:pn-rdk/components/generic/tr69hostif/generic+SRCREV:pn-tr69hostif +g' ${PWD}/../versions.conf
        sed -i 's+SRCREV:pn-components/opensource/westeros+SRCREV:pn-westeros+g' ${PWD}/../versions.conf
        sed -i 's+SRCREV:pn-rdk/components/generic/sys_mon_tools/analyzers/scripts/host/generic+SRCREV:pn-analyzers-host+g' ${PWD}/../versions.conf
        sed -i 's+SRCREV:pn-rdk/components/generic/dcm/generic+SRCREV:pn-dcmjsonparser+g' ${PWD}/../versions.conf
        sed -i 's+SRCREV:pn-rdk/components/generic/sys_mon_tools/si_cache_parser/generic+SRCREV:pn-si-cache-parser+g' ${PWD}/../versions.conf
        sed -i 's+SRCREV:pn-rdk/components/generic/rmf_tools/tenableHDCP/soc/broadcom/common+SRCREV_soc:pn-tenablehdcp+g' ${PWD}/../versions.conf
        sed -i 's+SRCREV:pn-rdk/components/generic/sys_mon_tools/mfr_data/generic+SRCREV:pn-mfr-data+g' ${PWD}/../versions.conf
        sed -i 's+SRCREV:pn-rdk/components/opensource/base64/generic+SRCREV:pn-base64+g' ${PWD}/../versions.conf
        sed -i 's+SRCREV:pn-rdk/components/generic/mocahal/generic+SRCREV:pn-moca-hal+g' ${PWD}/../versions.conf
        sed -i 's+SRCREV:pn-rdk/components/generic/sys_mon_tools/rdklogctrl/generic+SRCREV:pn-rdklogctrl+g' ${PWD}/../versions.conf
        sed -i 's+SRCREV:pn-rdk/components/generic/dtcp/generic+SRCREV:pn-dtcpmgr-noop+g' ${PWD}/../versions.conf
        sed -i 's+SRCREV:pn-rdk/components/generic/iarmbus/generic+SRCREV:pn-iarmbus+g' ${PWD}/../versions.conf
        sed -i 's+SRCREV:pn-nanomsg/nanomsg.git+SRCREV:pn-nanomsg+g' ${PWD}/../versions.conf
        sed -i 's+SRCREV:pn-rdk/components/generic/breakpad_wrapper/generic+SRCREV:pn-breakpad-wrapper+g' ${PWD}/../versions.conf
        sed -i 's+SRCREV:pn-rdk/components/generic/lxccpid+SRCREV:pn-lxccpid+g' ${PWD}/../versions.conf
        sed -i 's+SRCREV:pn-rdk/components/generic/iarmmgrs/generic+SRCREV:pn-iarmmgrs +g' ${PWD}/../versions.conf
        sed -i 's+SRCREV:pn-rdk/components/cpc/authservice/generic+SRCREV_authservice:pn-authservice+g' ${PWD}/../versions.conf
        sed -i 's+SRCREV:pn-rdk/components/generic/control-testapp/generic+SRCREV:pn-ctrlm-testapp+g' ${PWD}/../versions.conf
        sed -i 's+SRCREV:pn-rdk/components/thirdparty/vnc/generic+SRCREV:pn-vnc+g' ${PWD}/../versions.conf
        sed -i 's+SRCREV:pn-rdk/components/generic/hdmicec/soc/raspberrypi/rpi3+SRCREV:pn-hdmicec-hal+g' ${PWD}/../versions.conf
        sed -i 's+SRCREV:pn-rdk/components/generic/sec-apis/generic+SRCREV:pn-secapi-noop+g' ${PWD}/../versions.conf
        sed -i 's+SRCREV:pn-rdk/components/generic/dcm/generic+SRCREV:pn-dcmjsonparser+g' ${PWD}/../versions.conf
        sed -i 's+SRCREV:pn-rdk/components/generic/gstreamer-netflix-platform/generic+SRCREV:pn-rdk-gstreamer-utils+g' ${PWD}/../versions.conf
        sed -i 's+SRCREV:pn-rdk/components/generic/media_utils/generic+SRCREV:pn-media-utils+g' ${PWD}/../versions.conf
        sed -i 's+SRCREV:pn-rdk/components/opensource/jquery/generic+SRCREV:pn-jquery+g' ${PWD}/../versions.conf
        sed -i 's+SRCREV:pn-rdk/components/generic/sec-apis/generic+SRCREV:pn-secapi-noop+g' ${PWD}/../versions.conf
        sed -i 's+SRCREV:pn-rdk/components/generic/sys_mon_tools/iarm_set_powerstate/generic+SRCREV:pn-iarm-set-powerstate+g' ${PWD}/../versions.conf
        sed -i 's+SRCREV:pn-rdk/components/generic/netmonitor/generic+SRCREV:pn-nlmonitor+g' ${PWD}/../versions.conf
        sed -i 's+SRCREV:pn-rdk/components/generic/libSyscallWrapper/generic+SRCREV:pn-libsyswrapper+g' ${PWD}/../versions.conf
        sed -i 's+SRCREV:pn-rdk/components/generic/rmf_tools/tenableHDCP/generic+SRCREV:pn-tenablehdcp+g' ${PWD}/../versions.conf
        sed -i 's+SRCREV:pn-rdk/components/generic/closedcaption/generic+SRCREV:pn-closedcaption+g' ${PWD}/../versions.conf
        sed -i 's+SRCREV:pn-rdk/components/generic/servicemanager/generic+SRCREV:pn-servicemanager+g' ${PWD}/../versions.conf
        sed -i 's+SRCREV:pn-rdk/components/generic/sys_mon_tools/mfr_utils/generic+SRCREV:pn-mfr-utils+g' ${PWD}/../versions.conf
        sed -i 's+SRCREV:pn-rdk/components/generic/closedcaption/soc/noop/common+SRCREV:pn-closedcaption-hal-noop+g' ${PWD}/../versions.conf
        sed -i 's+SRCREV:pn-rdk/components/generic/rbus/generic+SRCREV:pn-rbus+g' ${PWD}/../versions.conf
        sed -i 's+SRCREV:pn-rdk/components/generic/gst_svp_ext/generic+SRCREV:pn-gst-svp-ext+g' ${PWD}/../versions.conf
        sed -i 's+SRCREV:pn-rdk/components/generic/sys_mon_tools/generate_si_cache/generic+SRCREV:pn-generate-si-cache+g' ${PWD}/../versions.conf
        sed -i 's+SRCREV:pn-rdk/components/generic/aampabr+SRCREV:pn-aampabr+g' ${PWD}/../versions.conf
        sed -i 's+SRCREV:pn-rdk/components/generic/rdkversion/generic+SRCREV:pn-rdkversion+g' ${PWD}/../versions.conf
        sed -i 's+SRCREV:pn-rdk/components/generic/servicemanager/soc/raspberrypi/raspberrypi+SRCREV_svcmgrplat:pn-servicemanager+g' ${PWD}/../versions.conf
        sed -i 's+SRCREV:pn-rdk/components/generic/mediaframework/soc/raspberrypi/common+SRCREV:pn-rmfhalheaders+g' ${PWD}/../versions.conf
        sed -i 's+SRCREV:pn-rdk/components/generic/sys_mon_tools/iarm_event_sender/generic+SRCREV:pn-iarm-event-sender+g' ${PWD}/../versions.conf
        sed -i 's+SRCREV:pn-rdk/components/generic/syslog_helper/generic+SRCREV:pn-syslog-helper+g' ${PWD}/../versions.conf
        sed -i 's+SRCREV:pn-rdk/components/generic/netsrvmgr/generic+SRCREV:pn-netsrvmgr+g' ${PWD}/../versions.conf
        sed -i 's+SRCREV:pn-rdk/components/generic/gst-plugins-rdk-aamp+SRCREV:pn-gst-plugins-rdk-aamp+g' ${PWD}/../versions.conf
        sed -i 's+SRCREV:pn-rdk/components/generic/injectedbundle/generic+SRCREV:pn-injectedbundle+g' ${PWD}/../versions.conf
        sed -i 's+SRCREV:pn-rdk/components/generic/sys_mon_tools/sys_utils/generic+SRCREV:pn-sys-utils+g' ${PWD}/../versions.conf
        sed -i 's+SRCREV:pn-rdk/components/cpc/tr69profiles/generic+SRCREV:pn-tr69profiles+g' ${PWD}/../versions.conf
        sed -i 's+SRCREV:pn-rdk/components/generic/bluetooth_leAppMgr/generic+SRCREV:pn-bluetooth-leappmgr+g' ${PWD}/../versions.conf
        sed -i 's+SRCREV:pn-rdk/components/generic/bluetooth_mgr/generic+SRCREV:pn-bluetooth-mgr+g' ${PWD}/../versions.conf
        sed -i 's+SRCREV:pn-rdk/components/generic/bluetooth/generic+SRCREV:pn-bluetooth-core+g' ${PWD}/../versions.conf
        sed -i 's+SRCREV:pn-rdk/components/generic/servicemanager/generic+SRCREV:pn-servicemanager+g' ${PWD}/../versions.conf
        sed -i 's+SRCREV:pn-rdk/components/generic/rdkmediaplayer/generic+SRCREV:pn-rdkmediaplayer+g' ${PWD}/../versions.conf
        sed -i 's+SRCREV:pn-zserge/jsmn.git+SRCREV:pn-jsmn+g' ${PWD}/../versions.conf
        sed -i 's+SRCREV:pn-rdk/components/cpc/appmanager/generic+SRCREV_cpcmgr:pn-appmanager+g' ${PWD}/../versions.conf
        sed -i 's+SRCREV:pn-rdk/components/generic/aamp+SRCREV:pn-aamp+g' ${PWD}/../versions.conf
        sed -i 's+SRCREV:pn-rdk/components/generic/rdm+SRCREV:pn-rdm+g' ${PWD}/../versions.conf
        sed -i 's+SRCREV:pn-rdk/components/cpc/authservice/devices/raspberrypi+SRCREV_authdevice:pn-authservice+g' ${PWD}/../versions.conf
        sed -i 's+SRCREV:pn-rdk/components/generic/sys_mon_tools/hostdataconverter/generic+SRCREV:pn-hostdataconverter+g' ${PWD}/../versions.conf
        sed -i 's+SRCREV:pn-rdk/components/generic/fonts/generic+SRCREV:pn-rdk-fonts+g' ${PWD}/../versions.conf
        sed -i 's+SRCREV:pn-rdk/components/generic/rdkapps/generic+SRCREV_rdkbrowserapps:pn-rdkbrowser+g' ${PWD}/../versions.conf
        sed -i 's+SRCREV:pn-rdk/components/generic/cpuprocanalyzer+SRCREV:pn-cpuprocanalyzer+g' ${PWD}/../versions.conf
        sed -i 's+SRCREV:pn-rdk/components/generic/dca/generic+SRCREV:pn-dca+g' ${PWD}/../versions.conf
        sed -i 's+SRCREV:pn-rdk/components/generic/sys_mon_tools/cgroup_memory_utils/generic+SRCREV:pn-cgrouputils+g' ${PWD}/../versions.conf
        sed -i 's+SRCREV:pn-rdk/components/generic/audiocapturemgr/generic+SRCREV:pn-audiocapturemgr+g' ${PWD}/../versions.conf
        sed -i 's+SRCREV:pn-rdk/components/generic/rdk_logger/generic+SRCREV:pn-rdk-logger+g' ${PWD}/../versions.conf
        sed -i 's+SRCREV:pn-rdk/components/cpc/sec-apis-crypto/generic+SRCREV:pn-secapi-crypto-brcm+g' ${PWD}/../versions.conf
        sed -i 's+SRCREV:pn-rdk/components/generic/gst-plugins-rdk/generic+SRCREV:pn-gst-plugins-rdk+g' ${PWD}/../versions.conf
        sed -i 's+SRCREV:pn-rdk/components/generic/sys_mon_tools/iarm_query_powerstate/generic+SRCREV:pn-iarm-query-powerstate+g' ${PWD}/../versions.conf
        sed -i 's+SRCREV:pn-rdk/components/generic/sys_mon_tools/analyzers/scripts/target/generic+SRCREV:pn-analyzers-target+g' ${PWD}/../versions.conf
        sed -i 's+SRCREV:pn-rdk/yocto_oe/layers/devicesettings-hal-sample+SRCREV:pn-devicesettings-hal-noop+g' ${PWD}/../versions.conf
        sed -i 's+SRCREV:pn-rdk/components/generic/control/generic+SRCREV:pn-ctrlm-main+g' ${PWD}/../versions.conf
        sed -i 's+SRCREV:pn-rdk/components/generic/sysint/generic+SRCREV:pn-sysint+g' ${PWD}/../versions.conf
        sed -i 's+SRCREV:pn-rdk/components/generic/fog/generic+SRCREV:pn-fog+g' ${PWD}/../versions.conf
        sed -i 's+SRCREV:pn-rdk/components/cpc/firewall/generic+SRCREV_firewall:pn-sysint+g' ${PWD}/../versions.conf
	sed -i 's+SRCREV:pn-rdk/components/cpc/playready-cdm-rdk/generic+SRCREV:pn-playready-cdm-rdk+g' ${PWD}/../versions.conf
	sed -i 's+SRCREV:pn-rdk/components/cpc/rdk-OCDM-Playready/generic+SRCREV:pn-wpeframework-ocdm-playready-rdk+g' ${PWD}/../versions.conf
	sed -i 's+SRCREV:pn-rdk/components/generic/rdkfwupdater+SRCREV:pn-rdkfwupgrader+g' ${PWD}/../versions.conf
	sed -i 's+SRCREV:pn-rdk/components/cpc/tdk/generic+SRCREV:pn-tdk-cpc+g' ${PWD}/../versions.conf
	sed -i 's+SRCREV:pn-rdk/components/cpc/config-files/generic+SRCREV:pn-config-files+g' ${PWD}/../versions.conf
	sed -i 's+SRCREV:pn-rdk/components/cpc/nvm/nvm-sqlite/generic+SRCREV:pn-nvm-sqlite+g' ${PWD}/../versions.conf
	sed -i 's+SRCREV:pn-rdk/components/cpc/config-libs/generic+SRCREV:pn-config-libs+g' ${PWD}/../versions.conf
	sed -i 's+SRCREV:pn-rdk/rdkb/components/cpc/icontrolkey/generic+SRCREV:pn-icontrolkey+g' ${PWD}/../versions.conf
	sed -i 's+SRCREV:pn-rdk/components/cpc/config-service/generic+SRCREV:pn-config-service+g' ${PWD}/../versions.conf
	sed -i 's+SRCREV:pn-rdk/components/cpc/sec_pks_client/generic+SRCREV:pn-sec-pks-client+g' ${PWD}/../versions.conf
	sed -i 's+SRCREV:pn-rdk/utils/cpc/sslcerts/generic+SRCREV:pn-sslcerts+g' ${PWD}/../versions.conf
	sed -i 's+SRCREV:pn-rdk/utils/cpc/sshkeys/generic+SRCREV:pn-sshkeys+g' ${PWD}/../versions.conf
	sed -i 's+SRCREV:pn-rdk/components/cpc/mount-utils/generic+SRCREV:pn-mount-utils+g' ${PWD}/../versions.conf
	sed -i 's+SRCREV:pn-rdk/components/cpc/ecfs_search/generic+SRCREV:pn-ecfs-search+g' ${PWD}/../versions.conf
	sed -i 's+SRCREV:pn-rdk/components/cpc/crypt_utils/generic+SRCREV:pn-crypt-utils+g' ${PWD}/../versions.conf
	sed -i 's+SRCREV:pn-rdk/components/cpc/snmp/generic+SRCREV:pn-net-snmp+g' ${PWD}/../versions.conf
	sed -i 's+SRCREV:pn-rdk/utils/cpc/snmpv3certs/generic+SRCREV:pn-snmpv3certs+g' ${PWD}/../versions.conf
	sed -i 's+SRCREV:pn-rdk/components/generic/podserver/devices/intel-x86-pc/rdkemulator/emu-podserver+SRCREV:pn-emu-podserver+g' ${PWD}/../versions.conf
	sed -i 's+SRCREV:pn-rdk/components/cpc/rtcast/generic+SRCREV:pn-rtcast+g' ${PWD}/../versions.conf
	sed -i 's+SRCREV:pn-rdk/components/thirdparty/cox/generic+SRCREV:pn-acscerts+g' ${PWD}/../versions.conf
	sed -i 's+SRCREV:pn-rdk/components/cpc/fonts/generic+SRCREV:pn-cpc-fonts+g' ${PWD}/../versions.conf
	sed -i 's+SRCREV:pn-rdk/component/cpc/xre-automation-dac15/xre-automation/devices/intel-x86-pc/rdkemulator +SRCREV:pn-fbdump-util+g' ${PWD}/../versions.conf
	sed -i 's+SRCREV:pn-rdk/components/cpc/netflix5_1/generic+SRCREV:pn-netflix+g' ${PWD}/../versions.conf
	sed -i 's+SRCREV:pn-rdk/components/generic/sdvagent/generic+SRCREV:pn-sdvagent+g' ${PWD}/../versions.conf
	sed -i 's+SRCREV:pn-rdk/components/cpc/rdkservices/generic+SRCREV:pn-rdkservices-comcast+g' ${PWD}/../versions.conf
	sed -i 's+SRCREV:pn-rdk/components/cpc/xdial/generic+SRCREV:pn-xdial+g' ${PWD}/../versions.conf
	sed -i 's+SRCREV:pn-rdk/component/cpc/mediaframework/devices/intel-x86-pc/halsnmp+SRCREV:pn-halsnmp-emu+g' ${PWD}/../versions.conf
	sed -i 's+SRCREV:pn-rdk/components/thirdparty/rogers/generic+SRCREV:pn-rogerscerts+g' ${PWD}/../versions.conf
	sed -i 's+SRCREV:pn-rdk/components/cpc/lostandfound/generic+SRCREV:pn-lostandfound+g' ${PWD}/../versions.conf
	sed -i 's+SRCREV:pn-rdk/component/cpc/widevine-cdm-rdk/generic+SRCREV:pn-widevinecdmi+g' ${PWD}/../versions.conf
	sed -i 's+SRCREV:pn-apps/netflix/rdkcryptoapi-netflix+SRCREV:pn-secapi-netflix+g' ${PWD}/../versions.conf
	sed -i 's+SRCREV:pn-rdk/components/cpc/playready/generic+SRCREV:pn-playreadycdmi+g' ${PWD}/../versions.conf
	sed -i 's+SRCREV:pn-rdk/components/cpc/playready_netflix/generic+SRCREV:pn-playready+g' ${PWD}/../versions.conf
	sed -i 's+SRCREV:pn-rdk/components/thirdparty/shaw/generic+SRCREV:pn-shawcerts+g' ${PWD}/../versions.conf
	sed -i 's+SRCREV:pn-rdk/components/cpc/nrdplugin/generic+SRCREV:pn-nrdplugin+g' ${PWD}/../versions.conf
	sed -i 's+SRCREV:pn-rdk/components/cpc/mount-utils/generic+SRCREV:pn-mountutils+g' ${PWD}/../versions.conf
	sed -i 's+SRCREV:pn-rdk/prototypes/rdkcef+SRCREV:pn-rdkcefapp+g' ${PWD}/../versions.conf
	sed -i 's+SRCREV:pn-rdk/components/opensource/chromium2062/generic+SRCREV:pn-cef-eglfs+g' ${PWD}/../versions.conf
	sed -i 's+SRCREV:pn-rdk/components/cpc/qtwebrtc_new/generic+SRCREV:pn-qtwebrtc+g' ${PWD}/../versions.conf
	sed -i 's+SRCREV:pn-rdk/components/generic/ppapi_plugins/generic+SRCREV:pn-ppapi-plugins+g' ${PWD}/../versions.conf
	sed -i 's+SRCREV:pn-rdk/components/cpc/OCDM-Playready/generic+SRCREV:pn-wpeframework-ocdm-playready+g' ${PWD}/../versions.conf
	sed -i 's+SRCREV:pn-rdk/components/cpc/cachehelpers/generic +SRCREV:pn-cachehelpers+g' ${PWD}/../versions.conf
	sed -i 's+SRCREV:pn-rdk/components/cpc/rdkssa/generic+SRCREV:pn-rdk-oss-ssa+g' ${PWD}/../versions.conf
	sed -i 's+SRCREV:pn-rdk/components/cpc/generic/libledger+SRCREV:pn-libledger+g' ${PWD}/../versions.conf
	sed -i 's+SRCREV:pn-rdk/components/cpc/ssa-cpc+SRCREV:pn-ssacpc+g' ${PWD}/../versions.conf
	sed -i 's+SRCREV:pn-rdk/components/cpc/generic/rdkxpkiutl+SRCREV:pn-rdkxpkiutl+g' ${PWD}/../versions.conf
	sed -i 's+SRCREV:pn-rdk/components/generic/ermgr+SRCREV:pn-ermgr+g' ${PWD}/../versions.conf
	sed -i 's+SRCREV:pn-rdk/components/generic/gstreamer-netflix-platform/generic+SRCREV:pn-rdk-gstreamer-utils+g' ${PWD}/../versions.conf
	sed -i 's+SRCREV:pn-rdk/components/generic/WebconfigFramework/generic+SRCREV:pn-webconfig-framework+g' ${PWD}/../versions.conf
	sed -i 's+SRCREV:pn-rdk/components/generic/audiocapturemgr/soc/raspberrypi/common+SRCREV_soc:pn-audiocapturemgr+g' ${PWD}/../versions.conf
	sed -i 's+SRCREV:pn-rdk/components/generic/bluetooth_leAppMgr/soc/raspberrypi/common+SRCREV_soc:pn-bluetooth-leappmgr+g' ${PWD}/../versions.conf
	sed -i 's+SRCREV:pn-rdk/components/cpc/bluetooth_leAppMgr+SRCREV_cpc:pn-bluetooth-leappmgr+g' ${PWD}/../versions.conf
	sed -i 's+SRCREV:pn-rdk/components/generic/bluetooth_mgr/soc/raspberrypi/common+SRCREV_soc:pn-bluetooth-mgr+g' ${PWD}/../versions.conf
	sed -i 's+SRCREV:pn-rdk/components/generic/devicesettings/devices/raspberrypi/rpi3+SRCREV:pn-devicesettings-hal-raspberrypi+g' ${PWD}/../versions.conf
	sed -i 's+SRCREV:pn-rdk/rdkb/components/opensource/ccsp/DSLAgent/generic+SRCREV:pn-ccsp-dslagent+g' ${PWD}/../versions.conf
	sed -i 's+SRCREV:pn-rdk/rdkb/components/opensource/ccsp/VLANAgent/generic+SRCREV:pn-ccsp-vlanagent+g' ${PWD}/../versions.conf
	sed -i 's+SRCREV:pn-rdk/rdkb/components/opensource/ccsp/XTMAgent/generic+SRCREV:pn-ccsp-xtmagent+g' ${PWD}/../versions.conf
	sed -i 's+SRCREV:pn-rdkb/components/opensource/ccsp/RebootManager+SRCREV:pn-reboot-manager+g' ${PWD}/../versions.conf
	sed -i 's+SRCREV:pn-rdk/components/rdkssa/generic+SRCREV:pn-rdk-oss-ssa+g' ${PWD}/../versions.conf
	sed -i 's+SRCREV:pn-rdk/yocto_oe/manifests/raspberrypi-manifest+SRCREV:pn-manifest+g' ${PWD}/../versions.conf 
        sed -i 's+SRCREV:pn-rdk/components/generic/media_interface_lib/soc/broadcom/common+SRCREV:pn-media-interface+g' ${PWD}/../versions.conf
fi

if [ -f ${PWD}/../versions.conf ]; then
     cat ${PWD}/../versions.conf >> ${PWD}/../auto.conf
fi

if [ -f ${PWD}/../auto.conf ]; then
     sed -i 's/externalsrc//g' ${PWD}/../auto.conf
     sed -i '/EXTERNALSRC_pn/d' ${PWD}/../auto.conf
     sed -i 's/SRCREV_pn-audioserver-sample-apps/SRCREV_audioserversampleapps_pn-audioserver-sample-apps/g'  ${PWD}/../auto.conf
     sed -i 's/SRCREV_pn-audioserver-headers/SRCREV_audioserverheaders_pn-audioserver-headers/g'  ${PWD}/../auto.conf
     sed -i 's/SRCREV_pn-audioserver /SRCREV_audioserver_pn-audioserver /g'  ${PWD}/../auto.conf
     sed -i 's/SRCREV_pn-rdm/SRCREV_rdmgeneric_pn-rdm/g'  ${PWD}/../auto.conf
     sed -i 's/SRCREV_pn-tdk/SRCREV_tdk_pn-tdk/g'  ${PWD}/../auto.conf
     sed -i 's/SRCREV_pn-jsmn/SRCREV_jsmn_pn-jsmn/g'  ${PWD}/../auto.conf  
     sed -i 's/SRCREV_pn-aampabr/SRCREV_aamp-abr:pn-aampabr/g'  ${PWD}/../auto.conf
     sed -i 's/SRCREV_pn-gst-plugins-rdk-aamp/SRCREV_gstplug-rdk-aamp:pn-gst-plugins-rdk-aamp/g'  ${PWD}/../auto.conf
     sed -i 's/SRCREV_pn-installtdk/SRCREV_tdk_pn-installtdk/g'  ${PWD}/../auto.conf
     sed -i 's/SRCREV_pn-ctrlm-headers/SRCREV_ctrlm-headers:pn-ctrlm-headers/g' ${PWD}/../auto.conf
     sed -i 's/SRCREV_pn-rbus-core/SRCREV_base_pn-rbus-core/g'  ${PWD}/../auto.conf
     sed -i 's/SRCREV_pn-hwselftest/SRCREV_hwselftest_pn-hwselftest/g'  ${PWD}/../auto.conf
     sed -i 's/SRCREV_pn-rtmessage/SRCREV_rtmessage_pn-rtmessage/g'  ${PWD}/../auto.conf
     sed -i 's/SRCREV_pn-libsyswrapper/SRCREV_libsyswrapper_pn-libsyswrapper/g'  ${PWD}/../auto.conf
     sed -i 's/SRCREV_pn-servicemanagerfunctionaltest/SRCREV_generic_pn-servicemanagerfunctionaltest/g'  ${PWD}/../auto.conf
     sed -i 's/SRCREV_pn-appmanager/SRCREV_generic_pn-appmanager/g'  ${PWD}/../auto.conf
     sed -i 's/SRCREV_pn-audiocapturemgr/SRCREV_audiocapturemgr_pn-audiocapturemgr/g'  ${PWD}/../auto.conf
     sed -i 's/SRCREV_pn-breakpad-wrapper/SRCREV_breakpad_wrapper_pn-breakpad-wrapper/g'  ${PWD}/../auto.conf
     sed -i 's/SRCREV_pn-ctrlm-testapp/SRCREV_ctrlm-testapp:pn-ctrlm-testapp/g'  ${PWD}/../auto.conf
     sed -i 's/SRCREV_pn-ctrlm-main/SRCREV_ctrlm-main:pn-ctrlm-main/g'  ${PWD}/../auto.conf
     sed -i 's/SRCREV_pn-hdmicec /SRCREV_hdmicec_pn-hdmicec /g'  ${PWD}/../auto.conf
     sed -i 's/SRCREV_pn-iarmbus/SRCREV_iarmbus_pn-iarmbus/g'  ${PWD}/../auto.conf
     sed -i 's/SRCREV_pn-iarmmgrs /SRCREV_iarmmgrs_pn-iarmmgrs /g'  ${PWD}/../auto.conf
     sed -i 's/SRCREV_pn-rmfgeneric /SRCREV_rmfgeneric_pn-rmfgeneric /g'  ${PWD}/../auto.conf
     sed -i 's/SRCREV_pn-nlmonitor/SRCREV_netmonitor_pn-nlmonitor/g'  ${PWD}/../auto.conf
     sed -i 's/SRCREV_pn-netsrvmgr/SRCREV_netsrvmgr_pn-netsrvmgr/g'  ${PWD}/../auto.conf
     sed -i 's/SRCREV_pn-rfc/SRCREV_rfc_pn-rfc/g'  ${PWD}/../auto.conf
     sed -i 's/SRCREV_pn-rmfstreamer/SRCREV_rmfmediastreamergeneric_pn-rmfstreamer/g'  ${PWD}/../auto.conf
     sed -i 's/SRCREV_pn-mfr-utils/SRCREV_mfr-utils:pn-mfr-utils/g'  ${PWD}/../auto.conf
     sed -i 's/SRCREV_pn-sysint/SRCREV_sysintgeneric_pn-sysint/g'  ${PWD}/../auto.conf
     sed -i 's/SRCREV_pn-tr69hostif /SRCREV_tr69hostif_pn-tr69hostif /g'  ${PWD}/../auto.conf
     sed -i 's/SRCREV_pn-wsproxy/SRCREV_wsproxy_pn-wsproxy/g'  ${PWD}/../auto.conf
     sed -i 's/SRCREV_pn-xupnp/SRCREV_xupnp_pn-xupnp/g'  ${PWD}/../auto.conf
     sed -i 's/SRCREV_pn-servicemanager/SRCREV_servicemanager_pn-servicemanager/g'  ${PWD}/../auto.conf
     sed -i 's/SRCREV_pn-gst-svp-ext/SRCREV_gst_pn-gst-svp-ext/g'  ${PWD}/../auto.conf
     sed -i 's/SRCREV_pn-ledmgr /SRCREV_generic_pn-ledmgr /g'  ${PWD}/../auto.conf
     sed -i 's/SRCREV_pn-dvrmgr /SRCREV_dvrmgr_pn-dvrmgr/g' ${PWD}/../auto.conf
     sed -i 's/SRCREV_pn-gst-plugins-rdk-dvr/SRCREV_dvr-plugins:pn-gst-plugins-rdk-dvr/g' ${PWD}/../auto.conf
     sed -i 's/SRCREV_pn-audioserver-gstplugin-generic/SRCREV_plugin_pn-audioserver-gstplugin-generic/g' ${PWD}/../auto.conf
     sed -i 's/SRCREV_pn-xr-sm-engine/SRCREV_xrpSMEngine_pn-xr-sm-engine/g' ${PWD}/../auto.conf
     sed -i 's/SRCREV_pn-libunpriv/SRCREV_libunpriv_pn-libunpriv/g' ${PWD}/../auto.conf
     sed -i 's/SRCREV_pn-rdkresidentapp/SRCREV_generic_pn-rdkresidentapp/g' ${PWD}/../auto.conf
     sed -i 's/SRCREV_pn-ctrlm-xraudio-hal /SRCREV_ctrlm-xraudio-hal:pn-ctrlm-xraudio-hal/g' ${PWD}/../auto.conf     
     sed -i 's/SRCREV_pn-ctrlm-xraudio-hal-headers/SRCREV_ctrlm-xraudio-hal-headers:pn-SRCREV_ctrlm-xraudio-hal-headers/g' ${PWD}/../auto.conf 
     sed -i 's/SRCREV_pn-tdk-cpc/SRCREV_tdk_tdkcpc_pn-tdk-cpc/g' ${PWD}/../auto.conf
     sed -i 's/SRCREV_pn-config-files/SRCREV_cpgc_pn-config-files/g' ${PWD}/../auto.conf
     sed -i 's/SRCREV_pn-systimemgrinetrface/SRCREV_systemtimemgrifc_pn-systimemgrinetrface/g' ${PWD}/../auto.conf
     sed -i 's/SRCREV_pn-systimemgr /SRCREV_systemtimemgr_pn-systimemgr/g' ${PWD}/../auto.conf
     sed -i 's/SRCREV_pn-systimemgrfactory/SRCREV_systemtimemgrfactory_pn-systimemgrfactory/g' ${PWD}/../auto.conf
     sed -i 's/SRCREV_pn-nvm-sqlite/SRCREV_nvm-sqlite:pn-nvm-sqlite/g' ${PWD}/../auto.conf
     sed -i 's/SRCREV_pn-rdkversion/SRCREV_rdkversion_pn-rdkversion/g' ${PWD}/../auto.conf
     sed -i 's/SRCREV_pn-config-libs/SRCREV_cpg-libs:pn-config-libs/g' ${PWD}/../auto.conf
     sed -i 's/SRCREV_pn-config-service/SRCREV_cpgu_pn-config-service/g' ${PWD}/../auto.conf
     sed -i 's/SRCREV_pn-ecfs-search/SRCREV_ecfsgeneric_pn-ecfs-search/g' ${PWD}/../auto.conf
     sed -i 's/SRCREV_pn-caupdate/SRCREV_caupdate_pn-caupdate/g' ${PWD}/../auto.conf
     sed -i 's/SRCREV_pn-rdk-ca-store/SRCREV_rdk-ca-store:pn-rdk-ca-store/g' ${PWD}/../auto.conf
     sed -i 's/SRCREV_pn-tdksm/SRCREV_tdkadvanced_pn-tdksm/g' ${PWD}/../auto.conf
     sed -i 's/SRCREV_pn-tdkadvanced/SRCREV_tdkadvanced_pn-tdkadvanced/g' ${PWD}/../auto.conf
     sed -i 's/SRCREV_pn-mfr-data/SRCREV_mfr-data:pn-mfr-data/g' ${PWD}/../auto.conf
     sed -i 's/SRCREV_pn-ctrlm-main/SRCREV_ctrlm-main-cpc:pn-ctrlm-main/g' ${PWD}/../auto.conf
     sed -i 's/SRCREV_pn-lostandfound/SRCREV_lafgeneric_pn-lostandfound/g' ${PWD}/../auto.conf
     sed -i 's/SRCREV_pn-widevinecdmi/SRCREV_widevine-cdm-rdk:pn-widevinecdmi/g' ${PWD}/../auto.conf
     sed -i 's/SRCREV_pn-playreadycdmi/SRCREV_playready_pn-playreadycdmi/g' ${PWD}/../auto.conf
     sed -i 's/SRCREV_pn-secclient/SRCREV_secclient_pn-secclient/g' ${PWD}/../auto.conf
     sed -i 's/SRCREV_pn-socprovapi-crypto/SRCREV_socprovapi-crypto:pn-socprovapi-crypto/g' ${PWD}/../auto.conf
     sed -i 's/SRCREV_pn-socprovapi /SRCREV_socprovapi_pn-socprovapi/g' ${PWD}/../auto.conf
     sed -i 's/SRCREV_pn-hwselftest/SRCREV_hwselftest_pn-hwselftest/g' ${PWD}/../auto.conf
     sed -i 's/SRCREV_pn-mountutils/SRCREV_generic_pn-mountutils/g' ${PWD}/../auto.conf
     sed -i 's/SRCREV_pn-libledger/SRCREV_libledger_pn-libledger/g' ${PWD}/../auto.conf
     sed -i 's/SRCREV_pn-rdkssa/SRCREV_rdkssa_pn-rdkssa/g' ${PWD}/../auto.conf
     sed -i 's/SRCREV_pn-lxy /SRCREV_lxy_pn-lxy/g' ${PWD}/../auto.conf
     sed -i 's/SRCREV_pn-lxyupdate/SRCREV_lxy_pn-lxyupdate/g' ${PWD}/../auto.conf
     sed -i 's/SRCREV_pn-wayland-plugin-default/SRCREV_xreplgs_pn-wayland-plugin-default/g' ${PWD}/../auto.conf
     sed -i 's/SRCREV_pn-cef-eglfs/SRCREV_rdkcef_chromium_pn-cef-eglfs/g' ${PWD}/../auto.conf
     sed -i 's/SRCREV_pn-qtwebrtc/SRCREV_qtwebrtc_pn-qtwebrtc/g' ${PWD}/../auto.conf
     sed -i 's/SRCREV_pn-authservice/SRCREV_authservice_pn-authservice/g' ${PWD}/../auto.conf
     sed -i 's/SRCREV_pn-sdvagent/SRCREV_sdvagent_pn-sdvagent/g' ${PWD}/../auto.conf
     sed -i 's/SRCREV_pn-rdk-oss-ssa/SRCREV_rdk-oss-ssa:pn-rdk-oss-ssa/g' ${PWD}/../auto.conf
     sed -i 's/SRCREV_pn-ssacpc/SRCREV_ssacpc_pn-ssacpc/g' ${PWD}/../auto.conf
     sed -i 's/SRCREV_pn-rdkxpkiutl/SRCREV_rdkxpkiutl_pn-rdkxpkiutl/g' ${PWD}/../auto.conf
fi     

