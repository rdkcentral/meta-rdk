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
import json
import sys

max_group_size = None
temp_group_bit = '0b0'
supported_bits = []
supported_version_bits = []
bit_position_map = {}
supplementary_docs_list = []


# Processing the values based on individual subdocs
def supported_docs(group_id, docs):
    global temp_group_bit
    name = None
    bit_pos = None
    support = None
    version = None
    secondary_doc = None
    rbus_listener = None
    dest = None

    for count in range(0, len(docs)):
        key_count = 0
        # Individual subdocs are segregated
        for key, value in list(docs[count].items()):
            key_count += 1
            if key == "name":
                name = value
            if key == "bitposition":
                bit_pos = value
            if key == "support":
                support = value
            if key == "version":
                version = value
            if key == "secondary_doc":
                secondary_doc = value
            if key == "rbus_listener":
                rbus_listener = value
            if key == "dest":
                dest =value

            if (len(list(docs[count].keys()))) == key_count:
                if name is not None and bit_pos is not None and support is not None and version is not None :
                    if secondary_doc is not None and secondary_doc is True:
                        supplementary_docs(name)
                    if support is True:
                        # Used to process the bit values
                        decimal_converter(group_id, name, bit_pos, version, support)

                    # Used to populate the bit_position_map
                    bit_position(name, bit_pos, group_id, support, rbus_listener, dest)
                    name = None
                    bit_pos = None
                    support = None
                    version = None
                    rbus_listener = None
                    dest = None
                    secondary_doc = None
    # Resetting global value back to 0 for next group's subdocs
    temp_group_bit = "0b0"


def supplementary_docs(value):
    global supplementary_docs_list

    temp = str(value)
    supplementary_docs_list.append(temp)


def supported_versions(value, version):
    global supported_version_bits

    temp = str(value)+"-"+str(version)
    supported_version_bits.append(temp)


# Used in populating the bit_position_map
def bit_position(name, bit_pos, group_id, support, rbus_listener, dest):
    global bit_position_map
    
    if rbus_listener is True:
        temp_str = str(name) + ":" + str(bit_pos) + ":" + str(support).lower() + ":" + str(rbus_listener).lower() + ":" + str(dest).lower()
    else :
        temp_str = str(name) + ":" + str(bit_pos) + ":" + str(support).lower()

    if not bool(bit_position_map):
        bit_position_map[group_id] = temp_str

    elif group_id in bit_position_map:
        temp_str = "," + temp_str
        prev_value = bit_position_map[group_id]
        current_value = prev_value + temp_str
        bit_position_map.update({group_id: current_value})

    else:
        bit_position_map.update({group_id: temp_str})


def decimal_converter(group_id, name, bit_pos, version, support):
    global supported_bits

    # values of single doc is loaded
    # print name
    # Below function is used to get the combined and individual Version values
    version_value = bit_calc(bit_pos, group_id)
    supported_bits[group_id - 1] = version_value[0]  # The version_value[0] holds combined version of a group

    if float(version) > 1.0 and support is True:
        supported_versions(version_value[1], version)  # version_value[1] holds individual doc version value


def bit_calc(bit_pos, group_id):
    global temp_group_bit

    individual_bin_value = "0b0"

    # left shifting 1 binary based on bit position
    temp_bin = 1
    temp_bin = temp_bin << (bit_pos - 1)
    temp_bit = bin(temp_bin)

    # getting bin converted value for both individual and combined versions in 24 bit format
    individual_bin_value = format(int(individual_bin_value, 2) | int(temp_bit, 2), '#026b')  # 026b gives 0b with 24 0's
    temp_group_bit = format(int(temp_group_bit, 2) | int(temp_bit, 2), '#026b')

    # Below functions is used to get the combined whole 32 bit version value with the MSB nibble of group_id
    final_value = int(append_bit(group_id, temp_group_bit), 2)
    final_indiv_value = int(append_bit(group_id, individual_bin_value), 2)
    return [final_value, final_indiv_value]


# Used to append the group_id nibble with the version bits
def append_bit(group_id, value):
    bin_value = format(group_id, '#010b')
    return bin_value+value[2:]


# Used to load the input json file
def local_main(src, dest, device_name):
    with open(src, "r") as json_file:
        data = json.load(json_file)
    json_read(data, dest, device_name)


# Used to parse the json file got from reading
def json_read(data, dest, device_name):
    global max_group_size
    global supported_bits
    group_id = None
    docs = None
    for (dev, subdoc) in list(data.items()):
        # Filtering based on device name from Json
        if dev in device_name:
            # print dev
            max_group_size = len(subdoc)
            supported_bits = [0 for i in range(0, max_group_size)]
            for count in range(0, len(subdoc)):
                for (key, value) in list(subdoc[count].items()):
                    if key == "group_id":
                        group_id = value

                    if key == "subdocs":
                        docs = value

                    if group_id is not None and docs is not None:
                        # Individual group is segregated
                        supported_docs(group_id, docs)
                        group_id = None
                        docs = None
            break

    '''print "Docs"
    for i in range(0,10):
        if supported_bits[i] != 0:
            print supported_bits[i]
    print "Version"
    for i in range(0, len(supported_version_bits)):
        print supported_version_bits[i]
    print "dict"
    print bit_position_map'''

    # Used to create the output_file
    if bool(bit_position_map) or bool(supplementary_docs_list):
        fout = open(dest, "w")
        fout.write("WEBCONFIG_SUPPORTED_DOCS_BIT=")
        for write_docs in range(0, len(supported_bits)):
            if supported_bits[write_docs] != 0:
                if write_docs != (len(supported_bits)) and write_docs != 0:
                    fout.write(",")
                fout.write(str(supported_bits[write_docs]))
        fout.write("\n")

        if len(supported_version_bits) > 0:
            fout.write("WEBCONFIG_DOC_SCHEMA_VERSION=")
            for write_version in range(0, len(supported_version_bits)):
                fout.write(supported_version_bits[write_version])
                if write_version != (len(supported_version_bits)-1):
                    fout.write(",")
            fout.write("\n")

        for write_bit in range(1,max_group_size+1):
            if write_bit in bit_position_map:
                fout.write("WEBCONFIG_SUBDOC_MAP_")
                fout.write(str(write_bit))
                fout.write("=")
                fout.write(bit_position_map[write_bit])
                fout.write("\n")

        if len(supplementary_docs_list) > 0:
            fout.write("WEBCONFIG_SUPPLEMENTARY_DOCS=")
            for docs_name in range (0, len(supplementary_docs_list)):
                fout.write(supplementary_docs_list[docs_name])
                if docs_name != (len(supplementary_docs_list)-1):
                    fout.write(",")
            fout.write("\n")

        fout.close()
        print("The "+dest+" file is created successfully\n")
    else:
        print("Either the device is not listed or it has no supported docs\n")


'''Start of the program which requires 3 arguments input_json output_file_location device_name'''

if len(sys.argv) != 4:
    print("usage: python input.json_file output_file_location device_name")
else:
    local_main(sys.argv[1], sys.argv[2], sys.argv[3])

