IMAGE_FEATURES[validitems] += " nightly tdk"

IMAGE_FEATURES += "read-only-rootfs"
IMAGE_EXTN =  "${@d.getVar("YOCTO_IMAGE_NAME_SUFFIX", True) or ""}"
IMAGE_NAME = "${MACHINE_IMAGE_NAME}${@bb.utils.contains("IMAGE_FEATURES", "tdk", "_TDK", "", d)}_${PROJECT_BRANCH}_${DATETIME}${IMAGE_EXTN}${@bb.utils.contains("IMAGE_FEATURES", "nightly", "_NG", "", d)}"

ROOTFS_POSTPROCESS_COMMAND += '${@bb.utils.contains("IMAGE_FEATURES", "read-only-rootfs", "rdk_read_only_rootfs_hook; ", "",d)}'
IMAGE_NAME[vardepsexclude] += "TIME DATE DATETIME"
rdk_read_only_rootfs_hook () {
}

R = "${IMAGE_ROOTFS}"

PROJECT_BRANCH ?= "default"


python version_hook(){
    bb.build.exec_func('create_version_file', d)
}

python create_version_file() {

    version_file = os.path.join(d.getVar("R", True), 'version.txt')
    image_name = d.getVar("IMAGE_NAME", True)
    machine = d.getVar("MACHINE", True).upper()
    branch = d.getVar("PROJECT_BRANCH", True)
    yocto_version = d.getVar("DISTRO_CODENAME", True)
    release_version = d.getVar("RELEASE_VERSION", True) or '0.0.0.0'
    release_spin = d.getVar("RELEASE_SPIN", True) or '0'
    stamp = d.getVar("DATETIME", True)
    t = time.strptime(stamp, '%Y%m%d%H%M%S')
    build_time = time.strftime('"%Y-%m-%d %H:%M:%S"', t)
    gen_time = time.strftime('Generated on %a %b %d  %H:%M:%S UTC %Y', t)
    extra_versions_path = d.getVar("EXTRA_VERSIONS_PATH", True)
    extra_version_files = []
    for (dirpath, dirnames, filenames) in os.walk(extra_versions_path):
        extra_version_files.extend(sorted(filenames))
        break
    extra_versions = []
    for filename in extra_version_files:
        with open(os.path.join(extra_versions_path, filename)) as fd:
            for line in fd.readlines():
                extra_versions.append(line)
    with open(version_file, 'w') as fw:
        fw.write('imagename:{0}\n'.format(image_name))
        fw.write('BRANCH={0}\n'.format(branch))
        fw.write('YOCTO_VERSION={0}\n'.format(yocto_version))
        fw.write('VERSION={0}\n'.format(release_version))
        fw.write('SPIN={0}\n'.format(release_spin))
        fw.write('BUILD_TIME={0}\n'.format(build_time))
        for version_string in extra_versions:
            fw.write("{0}\n".format(version_string.strip('\n')))
        fw.write('{0}\n'.format(gen_time))
    build_config = os.path.join(d.getVar("TOPDIR", True), 'build-images.txt')
    taskdata = d.getVar("BB_TASKDEPDATA", True)
    key = sorted(taskdata)[0] 
    target = taskdata[key][0]
    line = '{0} - {1}\n'.format(target, image_name)
    with open(build_config, 'a') as fw:
        fw.write(line) 
}

create_version_file[vardepsexclude] += "DATETIME"
create_version_file[vardepsexclude] += "BB_TASKDEPDATA"

ROOTFS_POSTPROCESS_COMMAND += 'version_hook; '

inherit core-image

do_populate_sdk_ext:prepend() {
    builddir = d.getVar('TOPDIR')
    if os.path.exists(builddir + '/conf/templateconf.cfg'):
        with open(builddir + '/conf/templateconf.cfg', 'w') as f:
            f.write('meta/conf\n')
}

