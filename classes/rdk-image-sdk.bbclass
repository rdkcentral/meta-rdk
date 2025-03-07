IMAGE_FEATURES[validitems] += " firebolt release"

#Firebolt SDK updates --- begin
#tar_sdk[prefuncs] += "do_rdk_artifact_customizations "
#Firebolt SDK updates --- end

#This function is included to avoid file name mismatch for release builds
def update_image_name(d):
    image_name = "${MACHINE_IMAGE_NAME}_FBT"
    if bb.utils.contains ("IMAGE_FEATURES", "tdk", True, False, d):
        image_name += "_TDK"
    image_name_suffix = d.getVar("YOCTO_IMAGE_NAME_SUFFIX", True) or ""

    if not (d.getVar("CPC_CUSTOM_STAMP", True) is None):
        customstamp = d.getVar("CPC_CUSTOM_STAMP", True)
        d.setVar("DATETIME", customstamp)
    if bb.utils.contains ( "IMAGE_FEATURES" , "release" , True, False, d):

        release_version = d.getVar("RELEASE_VERSION", True) or '0.0.0.0' #for comcast release combatibility
        release_version_values = release_version.split('.')
        version_info = [release_version_values[0], '.', release_version_values[1]]
        version_info = "".join(version_info)

        image_name += '_{0}'.format(version_info)
    else:
        branch = d.getVar("PROJECT_BRANCH", True)
        image_name +=  '_{0}'.format(branch)

    timestamp = d.getVar("DATETIME", True)

    image_name += '_{0}'.format(timestamp)

    if image_name_suffix :
        image_name += "_" + image_name_suffix

    if bb.utils.contains("IMAGE_FEATURES", "nightly", True, False, d):
        image_name += "_NG"

    return image_name

IMAGE_NAME = "${@update_image_name(d)}"
update_image_name[vardepsexclude] += "DATETIME"

def remove_oem_match_fname(basedir, match, logger):
    remove_file_escape(basedir, match, logger, False)

def remove_file_escape(basedir,match,logger,escape_re):
    import re

    try:
        if escape_re:
                match = re.escape(match)
        regex = re.compile(match);
        for root,dir,fnames in os.walk(basedir):
                for fname in fnames:
                        fh = os.path.join(root, fname)

                        if regex.match(fname):
                                os.remove(fh)
                                logger.write("Removed file : " + fh + '\n')

    except:
        logger.write("Warning.. Ignoring " + del_file +'\n');
        pass

#functions for cleaning up firebolt SDK
fakeroot python do_rdk_artifact_customizations() {
    #We are attempting to remove all files part of restricted licenses here
    rreverse_folder  = d.getVar("PKGDATA_DIR", True)
    rreverse_folder += "/runtime-reverse"
    logger = open (d.getVar("WORKDIR", True) + '/temp/log.sdk_removedfiles','w')
    base_dir = d.getVar("SDK_OUTPUT", True)
    base_dir += d.getVar("SDKTARGETSYSROOT", True)

    if (os.path.isdir(rreverse_folder)):
        print ("metadata file does exists, Removing QT components ")
        removeall_qt_components(d, base_dir, rreverse_folder, logger)
    if (os.path.isdir(rreverse_folder)):
        print ("metadata file does exists, going for license based cleanup ")
        do_license_based_cleanup(d,rreverse_folder)
    else:
        print ("SDK generation failed; reason: metadata files does not exists, directory looked for : "+ rreverse_folder)
        raise RuntimeError ("Failed to identify license information")

    logger.close()
}
def replace_egl_header_files(d,base_dir,logger):
    from shutil import copyfile
    import subprocess

    from_dir= os.path.join(d.getVar("PKG_CONFIG_SYSROOT_DIR", True), "usr/include/")
    to_dir= os.path.join(base_dir, "usr/include/");
    logger.write("Copying EGL headers to standard location \n")
    # Only copy files that already existed. 17.3 SDK has GLES3, 15.3 only has GLES etc
    copy_if_present(os.path.join(to_dir, "api/EGL"), to_dir, "EGL", logger)
    copy_if_present(os.path.join(to_dir, "api/GLES"), to_dir, "GLES", logger)
    copy_if_present(os.path.join(to_dir, "api/GLES2"), to_dir, "GLES2", logger)
    copy_if_present(os.path.join(to_dir, "api/GLES3"), to_dir, "GLES3", logger)
    copy_if_present(os.path.join(to_dir, "api/KHR"), to_dir, "KHR", logger)

def copy_egl_req_files_brcm_dnfl(d,base_dir,logger):
    from shutil import copyfile
    import subprocess

    replace_egl_header_files(d,base_dir,logger)

    #now we need to remove X11 references from egl platfomr headers
    subprocess.call(['sed','-i','/X11\//d',base_dir+"/usr/include/EGL/eglplatform.h"])
    subprocess.call(['sed','-i','/typedef Display/c typedef void *EGLNativeDisplayType;',base_dir+"/usr/include/EGL/eglplatform.h"])
    subprocess.call(['sed','-i','/typedef Pixmap/c typedef void *EGLNativePixmapType;',base_dir+"/usr/include/EGL/eglplatform.h"])
    subprocess.call(['sed','-i','/typedef Window/c typedef void *EGLNativeWindowType;',base_dir+"/usr/include/EGL/eglplatform.h"])

    #dunfell has a sandbox model, so we need to pick files from individual components
    from_dir= os.path.join(d.getVar("COMPONENTS_DIR", True), d.getVar("MACHINE_ARCH", True))
    #Copy package configuration of egl also.
    to_dir= os.path.join(base_dir, "usr/lib/pkgconfig/");
    comp_dir=os.path.join(from_dir, "broadcom-refsw/usr/lib/pkgconfig/")
    logger.write("Copying "+ comp_dir + 'egl.pc' +" to "+ to_dir + 'egl.pc\n')
    copyfile(comp_dir + 'egl.pc', to_dir + 'egl.pc')
    copyfile(comp_dir + 'glesv2.pc', to_dir + 'glesv2.pc')

    logger.write("Copying "+ from_dir + '../libv3ddriver.so' +" to "+ to_dir + '../libv3ddriver.so\n')
    copyfile(comp_dir + '../libv3ddriver.so', to_dir + '../libv3ddriver.so')

    #wayland-egl.h is from wayland-egl-bnxs
    from_dir= os.path.join(d.getVar("COMPONENTS_DIR", True), d.getVar("TUNE_PKGARCH", True))
    comp_dir=os.path.join(from_dir, "wayland-egl-bnxs/usr/include/")
    to_dir= os.path.join(base_dir, "usr/include/");

    logger.write("Copying "+ comp_dir + 'wayland-egl.h' +" to "+ to_dir + 'wayland-egl.h\n')
    copyfile(comp_dir + 'wayland-egl.h', to_dir + 'wayland-egl.h')

    logger.write("Copying "+ comp_dir + '../lib/libwayland-egl.so.0.0.0' +" to "+ to_dir + '../lib/libwayland-egl.so.0.0.0\n')
    copyfile(comp_dir + '../lib/libwayland-egl.so.0.0.0', to_dir + '../lib/libwayland-egl.so.0.0.0')
    
    logger.write("Copying "+comp_dir + '../lib/pkgconfig/wayland-egl.pc '+ to_dir + '../lib/pkgconfig/wayland-egl.pc')
    copyfile(comp_dir + '../lib/pkgconfig/wayland-egl.pc', to_dir + '../lib/pkgconfig/wayland-egl.pc')

    #Now we need to create symlinks for libEGL.so libGLESV1.so libGLESV2.so libwayland-egl.so libwayland-egl.so.0
    cur_dir= os.getcwd()
    os.chdir( to_dir + '../lib/')
    if not os.path.exists('libEGL.so'):
        os.symlink('libv3ddriver.so','libEGL.so')
    if not os.path.exists('libGLESv1.so'):
        os.symlink('libv3ddriver.so','libGLESv1.so')
    if not os.path.exists('libGLESv2.so'):
        os.symlink('libv3ddriver.so','libGLESv2.so')
    if not os.path.exists('libwayland-egl.so'):
        os.symlink('libwayland-egl.so.0.0.0','libwayland-egl.so')
    os.chdir(cur_dir)


def copy_egl_req_files_brcm(d,base_dir,logger):
    from shutil import copyfile
    import subprocess

    replace_egl_header_files(d,base_dir,logger)

    from_dir= os.path.join(d.getVar("PKG_CONFIG_SYSROOT_DIR", True), "usr/include/")
    to_dir= os.path.join(base_dir, "usr/include/");


    #now we need to remove X11 references from egl platfomr headers
    subprocess.call(['sed','-i','/X11\//d',base_dir+"/usr/include/EGL/eglplatform.h"])
    subprocess.call(['sed','-i','/typedef Display/c typedef void *EGLNativeDisplayType;',base_dir+"/usr/include/EGL/eglplatform.h"])
    subprocess.call(['sed','-i','/typedef Pixmap/c typedef void *EGLNativePixmapType;',base_dir+"/usr/include/EGL/eglplatform.h"])
    subprocess.call(['sed','-i','/typedef Window/c typedef void *EGLNativeWindowType;',base_dir+"/usr/include/EGL/eglplatform.h"])

    logger.write("Copying "+ from_dir + 'wayland-egl.h' +" to "+ to_dir + 'wayland-egl.h\n')
    copyfile(from_dir + 'wayland-egl.h', to_dir + 'wayland-egl.h')

    logger.write("Copying "+ from_dir + '../lib/libv3ddriver.so' +" to "+ to_dir + '../lib/libv3ddriver.so\n')
    copyfile(from_dir + '../lib/libv3ddriver.so', to_dir + '../lib/libv3ddriver.so')
    logger.write("Copying "+ from_dir + '../lib/libwayland-egl.so.0.0.0' +" to "+ to_dir + '../lib/libwayland-egl.so.0.0.0\n')
    copyfile(from_dir + '../lib/libwayland-egl.so.0.0.0', to_dir + '../lib/libwayland-egl.so.0.0.0')


    #Now we need to create symlinks for libEGL.so libGLESV1.so libGLESV2.so libwayland-egl.so libwayland-egl.so.0
    cur_dir= os.getcwd()
    os.chdir( to_dir + '../lib/')
    if not os.path.exists('libEGL.so'):
        os.symlink('libv3ddriver.so','libEGL.so')
    if not os.path.exists('libGLESv1.so'):
        os.symlink('libv3ddriver.so','libGLESv1.so')
    if not os.path.exists('libGLESv2.so'):
        os.symlink('libv3ddriver.so','libGLESv2.so')
    if not os.path.exists('libwayland-egl.so'):
        os.symlink('libwayland-egl.so.0.0.0','libwayland-egl.so')
    os.chdir(cur_dir)

     #Copy package configuration of egl also.
    from_dir= os.path.join(d.getVar("PKG_CONFIG_SYSROOT_DIR", True), "usr/lib/pkgconfig/")
    to_dir= os.path.join(base_dir, "usr/lib/pkgconfig/");
    logger.write("Copying "+ from_dir + 'egl.pc' +" to "+ to_dir + 'egl.pc\n')
    copyfile(from_dir + 'egl.pc', to_dir + 'egl.pc')
    logger.write("Copying "+ from_dir + 'wayland-egl.pc' +" to "+ to_dir + 'wayland-egl.pc\n')
    copyfile(from_dir + 'wayland-egl.pc', to_dir + 'wayland-egl.pc')

#For realteck devices
def copy_egl_req_files_hank(d,base_dir,logger):
    from shutil import copyfile
    import subprocess

    replace_egl_header_files(d,base_dir,logger)

    #now we need to remove X11 references from egl platfomr headers
    subprocess.call(['sed','-i','/X11\//d',base_dir+"/usr/include/EGL/eglplatform.h"])
    subprocess.call(['sed','-i','/typedef Display/c struct gbm_device;\nstruct gbm_surface;\n\n',base_dir+"/usr/include/EGL/eglplatform.h"])
    subprocess.call(['sed','-i','/typedef Pixmap/c typedef struct gbm_device * EGLNativeDisplayType;\ntypedef struct gbm_surface * EGLNativeWindowType;\n',base_dir+"/usr/include/EGL/eglplatform.h"])
    subprocess.call(['sed','-i',
    '/typedef Window/c typedef void * EGLNativePixmapType;\n\ntypedef EGLNativeWindowType NativeWindowType;\ntypedef EGLNativePixmapType NativePixmapType;\ntypedef EGLNativeDisplayType NativeDisplayType;',
    base_dir+"/usr/include/EGL/eglplatform.h"])


def remove_dead_links (basedir, logger):
    for root,dir,fnames in os.walk(basedir):
       for fname in fnames:
           fh = os.path.join(root, fname)
           print (' Checking %s '  % fh)
           try:
                if(os.path.islink(fh) and not os.path.exists(fh)):
                        os.unlink(fh)
                        logger.write("Removed file : " + fh + '\n')
           except:
                logger.write("Warning.. Ignoring " + fh+'\n');
                pass

def removeall_qt_components(d, base_dir, rreverse_folder, logger):
    from glob import glob
    from os import path

    logger.write("Looking for qt components\n")
    srch_pattern =['qt*', 'libqt*']
    qt_components = [e for ptn in srch_pattern for e in glob(path.join(rreverse_folder,ptn))]
    for qtentry in qt_components:
        logger.write("Removing QT component : "+ qtentry +'\n')
        remove_all_from_package(d, base_dir, path.basename(qtentry), logger)

#function to remove empty directories from the SDK. This is necessary since
#the directories itself is not added as part of FILE_INFO section of components
def remove_empty_dirs(basedir, logger):
    for root,dir,fnames in os.walk(basedir, topdown=False):
       for directory in dir:
           dh = os.path.join(root, directory)
           try:
               if(os.listdir(dh) == []):
                   os.rmdir(dh)
                   logger.write("Removed empty directory : " + dh + '\n')
           except:
                logger.write("Warning.. Ignoring " + dh+'\n');
                pass

def remove_all_from_package(d,base_dir,entry,logger):
    logger.write("Removing package " +entry+ '\n')
    file_list = get_token_from_file (d,entry , "FILES_INFO").strip()
    if not file_list == "" :
        #so we have a list of files, now we need to filter by "
        files_array = file_list.split('\"')
        arr_len = len(files_array)
        skip_entry = True
        for file_entry in files_array:
            if not skip_entry:
                if(os.path.exists(base_dir+file_entry)):
                    logger.write("Removing entry " +base_dir+file_entry+ '\n')
                    try:
                        os.remove(base_dir+file_entry)
                    except OSError as e:
                        logger.write("** Warning :: failed to remove entry " +base_dir+file_entry + '  ::: ' + e.strerror +  '\n')

            skip_entry = not skip_entry

def copy_if_present(from_dir,to_dir,dir_to_check,logger):
    from shutil import move, rmtree

    tdir = os.path.join(to_dir, dir_to_check)
    if ( os.path.isdir(tdir) and (not os.listdir(tdir))):
        rmtree(tdir)
        move(from_dir, to_dir )
    else:
        logger.write("directory does not exists or not empty " +tdir)

def do_license_based_cleanup(d,rreverse_folder):
    #We are attempting to remove all files part of restricted licenses here
    base_dir = d.getVar("SDK_OUTPUT", True)
    base_dir += d.getVar("SDKTARGETSYSROOT", True)
    logger = open (d.getVar("WORKDIR", True) + '/temp/log.sdk_removedfiles','w')
    machine_overrides = d.getVar("MACHINEOVERRIDES", True)
    distro_features = d.getVar("DISTRO_FEATURES" , True)

    for component in os.listdir(rreverse_folder):
       remove_if_retricted_license(d,base_dir,component,logger)

    #clean up systemd and other components
    do_rootfs_cleanup(base_dir,logger)

    if "brcm" in machine_overrides:
        if "dunfell" in distro_features:
            copy_egl_req_files_brcm_dnfl(d,base_dir,logger)
        else:
            copy_egl_req_files_brcm(d,base_dir,logger)
    elif "hank" in machine_overrides:
        copy_egl_req_files_hank(d,base_dir,logger)
    #we are forced to remove the library archive files since it contains
    # references to oem libraries, causing build failures
        remove_oem_match_fname(base_dir, ".*\.la$", logger)
    #Finally remove any links that are not linked properly
        remove_dead_links(base_dir +"/usr/lib", logger)
        remove_empty_dirs(base_dir,logger)
    logger.close()

def get_token_from_file(d,component,token_to_search):
    import os.path

    pkg_data_file = d.getVar("PKGDATA_DIR", True)
    pkg_data_file += "/runtime-reverse/"
    pkg_data_file += component

    if not os.path.isfile(pkg_data_file):
        return ""
    with open(pkg_data_file) as pkg_handle:
        for entry in pkg_handle:
            if entry.startswith(token_to_search):
                data = entry.split(':',1)[1]
                break

    pkg_handle.close()
    return data

def remove_if_retricted_license(d,base_dir,entry, logger):

    restricted_licenses = ["CLOSED", "COMCAST", "BROADCOM", "CPC", "ADOBE", "SEACHANGE", "GREENPEAK" ]
    license_type = ""

    license_type = get_token_from_file (d,entry, "LICENSE").upper()
    if any (lic_entry in license_type for lic_entry in restricted_licenses):
        logger.write("Removing entries from component :: " + entry +'\n')
        remove_all_from_package(d,base_dir,entry,logger)
        return True
    return False

def remove_all_from_package(d,base_dir,entry,logger):
    file_list = get_token_from_file (d,entry , "FILES_INFO").strip()
    if not file_list == "" :
        #so we have a list of files, now we need to filter by "
        files_array = file_list.split('\"')
        arr_len = len(files_array)
        skip_entry = True
        for file_entry in files_array:
            if not skip_entry:
                if(os.path.exists(base_dir+file_entry)):
                    logger.write("Removing entry " +base_dir+file_entry+ '\n')
                    try:
                        os.remove(base_dir+file_entry)
                    except OSError as e:
                        logger.write("** Warning :: failed to remove entry " +base_dir+file_entry + '  ::: ' + e.strerror +  '\n')

            skip_entry = not skip_entry

update_device_properties() {
    if [ "x${RNE_SUPPORT}" = "xtrue" ] && [ -f ${R}/etc/device.properties ];then
        echo "RNE_SUPPORT=true" >> ${R}/etc/device.properties
    fi
}

def do_rootfs_cleanup(base_dir, logger):
    remove_dir_rec(base_dir,"bin", logger)
    remove_dir_rec(base_dir,"boot", logger)
    remove_dir_rec(base_dir,"common", logger)
    remove_dir_rec(base_dir,"dev", logger)
    remove_dir_rec(base_dir,"etc", logger)
    remove_dir_rec(base_dir,"home", logger)
    remove_dir_rec(base_dir,"HrvInitScripts", logger)
    remove_dir_rec(base_dir,"media", logger)
    remove_dir_rec(base_dir,"mnt", logger)
    remove_dir_rec(base_dir,"opt", logger)
    remove_dir_rec(base_dir,"proc", logger)
    remove_dir_rec(base_dir,"QueryPowerState", logger)
    remove_dir_rec(base_dir,"rdklogctrl", logger)
    remove_dir_rec(base_dir,"rebootNow.sh", logger)
    remove_dir_rec(base_dir,"rebootSTB.sh", logger)
    remove_dir_rec(base_dir,"r.sh", logger)
    remove_dir_rec(base_dir,"run", logger)
    remove_dir_rec(base_dir,"sbin", logger)
    remove_dir_rec(base_dir,"SetPowerState", logger)
    remove_dir_rec(base_dir,"srv", logger)
    remove_dir_rec(base_dir,"sys", logger)
    remove_dir_rec(base_dir,"tmp", logger)
    remove_dir_rec(base_dir,"usb", logger)
    remove_dir_rec(base_dir,"usb0", logger)
    remove_dir_rec(base_dir,"usb1", logger)
    remove_dir_rec(base_dir,"var", logger)
    remove_dir_rec(base_dir,"www", logger)

    remove_dir_rec(base_dir,"usr/bin", logger)
    remove_dir_rec(base_dir,"usr/firmware", logger)
    remove_dir_rec(base_dir,"usr/games", logger)
    remove_dir_rec(base_dir,"usr/local", logger)
    remove_dir_rec(base_dir,"usr/sbin", logger)
    remove_dir_rec(base_dir,"usr/share", logger)
    remove_dir_rec(base_dir,"usr/src", logger)
    remove_dir_rec(base_dir,"usr/lib/systemd", logger)
    remove_dir_rec(base_dir,"usr/lib/ssl", logger)

    remove_dir_rec(base_dir,"lib/systemd", logger)
    remove_dir_rec(base_dir,"lib/modules", logger)
    remove_dir_rec(base_dir,"lib/udev", logger)
    remove_dir_rec(base_dir,"lib/firmware", logger)
    remove_dir_rec(base_dir,"lib/.debug", logger)
    remove_dir_rec(base_dir,"lib/modprobe.d", logger)
    remove_dir_rec(base_dir,"lib/depmod.d", logger)
    remove_dir_rec(base_dir,"lib/rdk", logger)
    remove_dir_rec(base_dir,"usr/lib/opkg", logger)
    remove_dir_rec(base_dir,"usr/lib/xtables", logger)
    remove_dir_rec(base_dir,"usr/lib/udev", logger)
    remove_dir_rec(base_dir,"usr/lib/tmpfiles.d", logger)
    remove_dir_rec(base_dir,"usr/lib/audit", logger)
    remove_dir_rec(base_dir,"usr/lib/modules-load.d", logger)

def remove_dir_rec(basedir,folder,logger):
    from shutil import rmtree
    f_todel = os.path.join(basedir,folder)
    if(os.path.isdir(f_todel)) :
       rmtree(os.path.join(basedir,folder))
       logger.write("Removed "+f_todel +'\n')
    elif os.path.isfile(f_todel):
       os.remove(f_todel)
       logger.write("Removed "+f_todel + '\n')
    elif os.path.islink(f_todel):
       os.unlink(f_todel)
       logger.write("Removed symoblic link "+f_todel + '\n')
    else:
       logger.write("Ignoring "+f_todel + '\n')



#We need to inherit from rdk-image to acquiure common code
inherit rdk-image
