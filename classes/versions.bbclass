do_fetch[postfuncs] += "write_version"
do_fetch[vardepsexclude] += "write_version"

SSTATEPOSTINSTFUNCS:append = " write_version"
sstate_install[vardepsexclude] += "write_version"
SSTATEPOSTINSTFUNCS[vardepvalueexclude] .= "| write_version"

python write_version() {
    scms = []
    fetcher = bb.fetch.Fetch(d.getVar('SRC_URI', True).split(), d, False)
    urldata = fetcher.ud
    for u in urldata:
        # for now we just want to gether info only about internal repositories
        #if urldata[u].method.supports_srcrev() and urldata[u].host == 'gerrit.teamccp.com:29418':
            scms.append(u)

    topdir = d.getVar('TOPDIR', True)
    versions_file = os.path.join(topdir, 'versions.txt')

    for scm in scms:
        ud = urldata[scm]
        for name in ud.names:
            with open(versions_file, 'a') as f:
              if ud.host == 'gerrit.teamccp.com:29418' or ud.type in ['git']:
                f.write('{0}://{1}{2}@{3} : {4}\n'.format(ud.proto, ud.host, ud.path, str(ud.branches[name]), str(ud.revisions[name])))
              else:
                if ud.type in ['http', 'https', 'ftp', 'sftp']:
                  f.write("{0}://{1}{2} : {3} {4}\n".format(ud.type, ud.host, ud.path, "md5sum", ud.md5_expected))
                elif ud.type in ['svn']:
                  f.write("{0}://{1}{2}@{3} : {4}\n".format(ud.type, ud.host, ud.path, ud.module, ud.revision))
}

addhandler remove_old_versions_file
remove_old_versions_file[eventmask] = "bb.event.BuildStarted"

python remove_old_versions_file() {
    topdir = e.data.getVar('TOPDIR', True)
    versions_file = os.path.join(topdir, 'versions.txt')

    if os.path.exists(versions_file):
        os.remove(versions_file)
}

addhandler versions_post_processing
versions_post_processing[eventmask] = "bb.event.BuildCompleted"

python versions_post_processing() {
    topdir = e.data.getVar('TOPDIR', True)
    versions_file = os.path.join(topdir, 'versions.txt')

    rf = open(versions_file, 'r')
    versions = set(rf.readlines())
    rf.close()

    layers = (e.data.getVar("BBLAYERS", True) or "").split()
    for layer in layers:
        revision = base_get_metadata_git_revision(layer, None)
        repo = get_metadata_git_repo(layer)

        if revision != '<unknown>' and repo != '<unknown>':
            versions.add('{0}@{1} : {2}\n'.format(repo, revision, revision))

    with open(versions_file, 'w') as f:
        f.write(''.join(versions))
}

def get_metadata_git_repo(path):
    from subprocess import Popen, PIPE

    p = Popen("cd {0}; git remote -v | grep fetch | cut -f 2 | cut -d ' ' -f 1".format(path), shell=True, stdout=PIPE)
    out, _ = p.communicate()
    if not p.returncode:
        return out.strip()

    return '<unknown>'

