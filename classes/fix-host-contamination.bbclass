# Fix host contamination by ensuring all files are owned by root before packaging
# This prevents "uid not found: 1006" type errors during do_package

# Run before do_package to fix any files with host uid/gid
fakeroot do_fix_ownership() {
    if [ -d "${PKGD}" ]; then
        # Change ownership of all files to root:root (0:0)
        chown -R 0:0 ${PKGD} || true
    fi
}

addtask fix_ownership after do_install before do_package do_package_qa
