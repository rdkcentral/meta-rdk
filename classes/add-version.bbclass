# ------------------------------------------------------------------------------
# Inherit this class to add version information to the ${R}/version.txt file.
# ------------------------------------------------------------------------------

do_add_version () {
    PN_UPPER=$(echo ${PN} | tr 'a-z' 'A-Z')
    echo "${PN_UPPER}=${PV}" > ${EXTRA_VERSIONS_PATH}/${PN}.txt
}
addtask add_version after do_add_version_dir before do_install

# Keep do_add_version_dir to make it easier to customize do_add_version.
do_add_version_dir () {
    if [ ! -d "${EXTRA_VERSIONS_PATH}" ]; then
        mkdir -p ${EXTRA_VERSIONS_PATH}
    fi
}
addtask add_version_dir after do_compile before do_add_version

# Remove version file during clean to avoid contamination.
python do_clean:append () {
    extra_versions_path = d.getVar("EXTRA_VERSIONS_PATH", True)
    pn = d.getVar("PN", True) + ".txt"
    version_pn = os.path.join(extra_versions_path, pn)
    if os.path.isfile(version_pn):
        os.remove(version_pn)
}
