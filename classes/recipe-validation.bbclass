do_fetch[postfuncs] += "validate_recipe"
do_fetch[vardepsexclude] += "validate_recipe"

SSTATEPOSTINSTFUNCS:append = " validate_recipe"
sstate_install[vardepsexclude] += "validate_recipe"
SSTATEPOSTINSTFUNCS[vardepvalueexclude] .= "| validate_recipe"

python validate_recipe() {
    import bb
    from   bb.fetch2 import FetchError
    scms = []
    fetcher = bb.fetch.Fetch(d.getVar('SRC_URI', True).split(), d)
    urldata = fetcher.ud
    for u in urldata:
       # Add info only about git repositories
       if urldata[u].method.supports_srcrev()  and urldata[u].type in ['git']:
          scms.append(u)

    # Check for SRCREV_FORMAT and name definition in case of multiple SCMs pointing to a GIT repository
    if len(scms) > 1:
       # Multiple git repositories are involved , so SRCREV_FORMAT should exist
       format = d.getVar('SRCREV_FORMAT', True)
       if not format:
          raise FetchError("The SRCREV_FORMAT variable must be set when multiple SCMs are used.")
       
       formatList=list(set(format.split('_'))) 
       pn = d.getVar("PN", True)
       pv = d.getVar("PV", True)
     
       for scm in scms:
          udata = urldata[scm]
          for name in udata.names:
             if not 'name' in udata.parm:
                bb.warn("Package Name : %s, Package Version : %s , Name should be defined for each SRC_URI pointing to a GIT repository." % (pn, pv))
                name = "default"
             try:
                formatList.remove(name)
             except ValueError:
                raise FetchError("Name %s is missing in SRCREV_FORMAT for SRC_URI :: %s , in package %s." % (name, udata.url, pn))
}

