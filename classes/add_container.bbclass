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
# Creating Container ipk and adding it to the application tar
# for downloadable module
# ------------------------------------------------------------------------------


do_rootfs[depends] += "lxc-container-generator-native:do_populate_sysroot"
TOOL_DIR="${STAGING_DATADIR_NATIVE}/lxc-container-generator"

ROOTFS_POSTPROCESS_COMMAND:append = "container_to_tar ;"

container_to_tar() {
        #Check if container directory exists
	  pkg_done_flag=".pkg_${type}_done"
          pkg_inprogress_flag="$HOME/package_deploy.lck"
          exec 8>$pkg_inprogress_flag
	  flock -x 8
	  bbnote "[RDM] Lock acquired Successfully even for tar to container function"
        if [ -d "${IMAGE_ROOTFS}/container" ]; then
                container_apps=$(ls ${IMAGE_ROOTFS}/container)
                DISTRO_ENABLED="${@bb.utils.contains('DISTRO_FEATURES','rdm','true','false',d)}"
                if [ $DISTRO_ENABLED = 'true' ]; then
                        for app in `echo ${container_apps}`; do
                                if [ -d ${IMAGE_ROOTFS}/container/$app ]; then
                                        DOBBY_ENABLED="${@bb.utils.contains('DISTRO_FEATURES','DOBBY_CONTAINERS','true','false',d)}"
                                        if [ $DOBBY_ENABLED = 'true' ] && [ $app = 'netflix' ]; then
                                                echo "skip netflix RDM package creation, Thunder plugins do not yet support RDM"
                                                continue
                                        fi
                                        echo "container_to_tar: Adding ${container_apps} to RDM package"
                                        if [ -f ${DEPLOY_DIR}/rdm-pkgs/${app}-pkg.tar ]; then
                                                loc=`pwd`
                                                cd  ${IMAGE_ROOTFS}/container/$app
                                                #Adding container rootfs,conf and launcher dirs to tar
                                                dobby_files=`if [ -f config.json ]; then echo config.json rootfs_dobby; fi`
                                                tar -cvzf data.tar.gz conf rootfs launcher $dobby_files
                                                ipk_name="${app}_container"
                                                ar -crfU ${ipk_name}.ipk data.tar.gz
                                                #Extracting and removing packages.list file from tar
                                                tar -rvf ${DEPLOY_DIR}/rdm-pkgs/${app}-pkg.tar ${ipk_name}.ipk
                                                echo "container_to_tar: extracting packages.list"
                                                tar -xvf ${DEPLOY_DIR}/rdm-pkgs/${app}-pkg.tar --wildcards --no-anchored 'packages.list'
                                                echo "container_to_tar: deleting packages.list"
                                                tar -vf ${DEPLOY_DIR}/rdm-pkgs/${app}-pkg.tar --delete --wildcards --no-anchored 'packages.list'
                                                #updating extracted packages.list file and appending to tar
                                                echo "$ipk_name.ipk" >> packages.list
                                                tar -rvf ${DEPLOY_DIR}/rdm-pkgs/${app}-pkg.tar packages.list
                                                hardened_xml_file=`find ${TOOL_DIR} -type f -iname lxc_conf_hardened_${app}.xml`
                                                non_hardened_xml_file=`find ${TOOL_DIR} -type f -iname lxc_conf_${app}.xml`
                                                if [ ! -z "$hardened_xml_file" ];then
							xml_file="$hardened_xml_file"
						elif [ ! -z "$non_hardened_xml_file" ];then
							xml_file="$non_hardened_xml_file"
						fi
                                                if [ ! -z "$xml_file" ] && [ -f $xml_file ];then
                                                        xmllint --xpath "//CONTAINER/LxcParams/ExecName/text()" $xml_file >> executables.txt
                                                        echo "" >> executables.txt
                                                        attach_count=$(xmllint --xpath 'count(//CONTAINER/LxcParams/Attach)' $xml_file)
                                                        count=1
                                                        while [ $count -le $attach_count ];do
                                                                xmllint --xpath "//CONTAINER/LxcParams/Attach[$count]/ExecName/text()" $xml_file >> executables.txt
                                                                echo "" >> executables.txt
                                                                count=`expr $count + 1`
                                                        done
                                                        cat ${IMAGE_ROOTFS}/container/$app/executables.txt >> ${IMAGE_ROOTFS}/tmp/xml.txt
                                                fi
                                                tar -rvf ${DEPLOY_DIR}/rdm-pkgs/${app}-pkg.tar executables.txt
                                                rm -rf ${IMAGE_ROOTFS}/container/$app/*
                                                mkdir pool/
                                                tar -xvf ${DEPLOY_DIR}/rdm-pkgs/${app}-pkg.tar
                                                ipks=`cat packages.list`
                                                for ipk in $ipks; do
                                                        if [ -f $ipk ];then
                                                                ar -x $ipk
                                                                if [ -f data.tar.gz ]; then
                                                                        tar --skip-old-files -xzvf data.tar.gz -C pool/
                                                                        rm -rf data.tar.gz
                                                                fi
                                                        fi
                                                done
                                                size=`du -sh pool/ | cut -f1`
                                                if [ -f ${IMAGE_ROOTFS}/etc/rdm/rdm-manifest.xml ];then
                                                        sed -i '/<'"$app"'>/,/<\/'"$app"'>/s/<app_size>.*/<app_size>'"$size"'<\/app_size>/' ${IMAGE_ROOTFS}/etc/rdm/rdm-manifest.xml
                                                fi
                                                cd $loc
                                                rm -rf ${IMAGE_ROOTFS}/container/$app
                                        fi
                                fi
                        done
                fi
        else
                echo "container_to_tar: container_to_tar invoked but container directory is missing!!!"
        fi

flock -u 8
              rm -f $pkg_inprogress_flag
              bbnote "[RDM] lock released successfully"

}

