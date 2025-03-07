#!/bin/sh
##########################################################################
# If not stated otherwise in this file or this component's LICENSE
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

DNLD_SCRIPT=/usr/bin/download_apps.sh
RDK_LOG_PATH=/rdklogs/logs
VIDEO_DEVICE_LOG_PATH=/opt/logs
LOG_FILE_NAME=debug_tools.log
APP_NAME_PREFIX=debug_tools
DBG_TOOL_DNLD_PATH=/tmp
LSOF_PATH=$DBG_TOOL_DNLD_PATH/$APP_NAME_PREFIX-lsof/usr/sbin
GPERFTOOLS_PATH=$DBG_TOOL_DNLD_PATH/$APP_NAME_PREFIX-gperftools
GPERFTOOLS_BIN_PATH=$DBG_TOOL_DNLD_PATH/$APP_NAME_PREFIX-gperftools/usr/bin
GPERFTOOLS_LIB_PATH=$DBG_TOOL_DNLD_PATH/$APP_NAME_PREFIX-gperftools/usr/lib
PERL_PATH=$GPERFTOOLS_LIB_PATH/perl5
STRACE_PATH=$DBG_TOOL_DNLD_PATH/$APP_NAME_PREFIX-strace/usr/bin
GDB_PATH=$DBG_TOOL_DNLD_PATH/$APP_NAME_PREFIX-gdb/usr/bin

# log format
DT_TIME=`date +'%Y-%m-%d:%H:%M:%S:%6N'`
echo_t()
{
    echo "$DT_TIME $@"
}

# Setting log path
if [ -d $RDK_LOG_PATH ];then
    DEBUG_LOG_FILE=$RDK_LOG_PATH/$LOG_FILE_NAME
elif [ -d $VIDEO_DEVICE_LOG_PATH ];then
    DEBUG_LOG_FILE=$VIDEO_DEVICE_LOG_PATH/$LOG_FILE_NAME
else
    DEBUG_LOG_FILE=/tmp/$LOG_FILE_NAME
fi

if [ ! -f $DEBUG_LOG_FILE ]; then
    touch $DEBUG_LOG_FILE
fi

clean_debug_tool()
{
    #Clean all downloaded debug tools in case tool name not specified
    if [ -z $1 ];then
        echo_t "[DBGTOOLSUPDATE] Executing cleanup of all installed debug tools " >> $DEBUG_LOG_FILE
        for toolname in lsof gperftools strace gdb
        do
            if [ -d $DBG_TOOL_DNLD_PATH/$APP_NAME_PREFIX-$toolname ];then
                sh $DNLD_SCRIPT $APP_NAME_PREFIX-$toolname $DBG_TOOL_DNLD_PATH cleanup
            fi
        done
    #Clean the debug tool specified in input
    else
        echo_t "[DBGTOOLSUPDATE] Executing cleanup of $1 debug tool" >> $DEBUG_LOG_FILE
        sh $DNLD_SCRIPT $APP_NAME_PREFIX-$1 $DBG_TOOL_DNLD_PATH cleanup
    fi
}

set_path_variable_lsof()
{
    if [ -d $LSOF_PATH ];then
        export PATH=$PATH:$LSOF_PATH
    fi
}

set_path_variable_gperftools ()
{
    if [ -d $GPERFTOOLS_BIN_PATH ];then
        #Setting binutils path
        export PATH=$PATH:$GPERFTOOLS_BIN_PATH
        if [ -d $GPERFTOOLS_LIB_PATH ];then
            export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:$GPERFTOOLS_LIB_PATH
        fi

        #Setting binutils path
        for dir in $GPERFTOOLS_PATH/usr/*
        do
            if [ $dir != $GPERFTOOLS_BIN_PATH ] && [ $dir != $GPERFTOOLS_LIB_PATH ] ;then
                binutils_path=$dir/bin
                if [ -d $binutils_path ];then
                    export PATH=$PATH:$binutils_path
                fi
            fi
        done
        #Setting perl5 path
        if [ -d $PERL_PATH ];then
            PERL_VERSION=$(ls $PERL_PATH)
            PERL_LIB_PATH=$PERL_PATH/$PERL_VERSION
            export PERL5LIB=$PERL5LIB:$PERL_LIB_PATH
        fi
    fi
}

set_path_variable_strace ()
{
    if [ -d $STRACE_PATH ];then
        export PATH=$PATH:$STRACE_PATH
    fi
}

set_path_variable_gdb ()
{
    if [ -d $GDB_PATH ];then
        export PATH=$PATH:$GDB_PATH
    fi
}

set_all_path_variables()
{
    #Set existing path variables of all the debug tools installed
    set_path_variable_lsof
    set_path_variable_gperftools
    set_path_variable_strace
    set_path_variable_gdb
}

#Set existing path variables if tool name is not mentioned as input
if [ -z $1 ]; then
    echo_t "[DBGTOOLSUPDATE] Tool name not specified, so setting the existing path variables if any" >> $DEBUG_LOG_FILE
    set_all_path_variables
#Cleanup the downloaded debug tools
elif [ $1 == "cleanall" ];then
  clean_debug_tool $2
#Download the requested debug tool
else
    TOOL_NAME=$1
    APP_NAME=$APP_NAME_PREFIX-$1
    sh $DNLD_SCRIPT $APP_NAME $DBG_TOOL_DNLD_PATH
    DNLD_RES=$?
    echo_t "[DBGTOOLSUPDATE] DebugTools $APP_NAME download result is:$DNLD_RES" >> $DEBUG_LOG_FILE
    if [ $DNLD_RES -eq 0 ]; then
        echo_t "[DBGTOOLSUPDATE] DebugTools $APP_NAME download successful, setting path variables" >> $DEBUG_LOG_FILE
        if [ $TOOL_NAME == "lsof" ];then
            set_path_variable_lsof
        elif [ $TOOL_NAME == "gperftools" ];then
            set_path_variable_gperftools
            #Correcting the links to binutils
            for dir in $GPERFTOOLS_PATH/usr/*
            do
                if [ $dir != $GPERFTOOLS_BIN_PATH ] && [ $dir != $GPERFTOOLS_LIB_PATH ] ;then
                    host_prefix=`echo $dir | awk -F\/ '{print $NF}'`
                    break
                fi
            done
            for fname in addr2line objdump nm c++filt
            do
                linktarget=$host_prefix-$fname
                sourcefile=$GPERFTOOLS_BIN_PATH/$linktarget
                symblink=$GPERFTOOLS_BIN_PATH/$fname
                ln -sf $sourcefile $symblink
            done
        elif [ $TOOL_NAME == "strace" ];then
            set_path_variable_strace
        elif [ $TOOL_NAME == "gdb" ];then
           set_path_variable_gdb
        else
            echo_t "[DBGTOOLSUPDATE] Requested Debug tool not supported: $APP_NAME download failed" >> $DEBUG_LOG_FILE
        fi
    else
        echo_t "[DBGTOOLSUPDATE] DebugTools $APP_NAME download failed" >> $DEBUG_LOG_FILE
    fi
fi
