python () {
    src_uri = d.getVar('SRC_URI', True)
    repos_host = d.getVar('REPOS_HOST', False)
    repos_codecentral="code.rdkcentral.com"
    if not repos_host:
        return

    if repos_host in src_uri or repos_codecentral in src_uri:
        code_reviews_val = d.getVar('REPO_SRCREV_OVERRIDE', False)
        if not code_reviews_val:
            return

        code_reviews = []
        for code_review_val in code_reviews_val.split(','):
            _partition = code_review_val.split()
            if len(_partition) == 3:
                code_reviews.append(tuple(_partition))
            else:
                # backward compatibility with old format: '<repo> <rev>'
                # the new froamt is '<repo> <branch/tag> <rev>'
                code_reviews.append((_partition[0], None, _partition[1]))

        new_src_uri = src_uri

        named_src_revs = {}
        for url in new_src_uri.split():
            fetch_type, host, path, usr, paswd, params = bb.fetch.decodeurl(url)

            name = params.get('name', 'default')

            for repo_name, branch, change_rev in code_reviews:
                if host == repos_codecentral:
                    if not repo_name == path[3:]:
                        continue
                else:
                    if not repo_name == path.strip('/'):
                        continue    

                if branch and params.get('branch', 'master') != branch:
                    continue

                params['branch'] = branch
                params['rev'] = change_rev
                params['nobranch'] = "1"

                if name not in named_src_revs:
                  named_src_revs[name] = []

                named_src_revs[name].append(change_rev)

                new_url = bb.fetch.encodeurl((fetch_type, host, path, usr, paswd, params))
                new_src_uri = new_src_uri.replace(url, new_url)

        if named_src_revs:
            if len(named_src_revs.keys()) > 1:
                for name, revs in named_src_revs.items():
                  d.setVar('SRCREV_{0}'.format(name), revs[-1])
                  d.setVar('SRCPV_{0}'.format(name), revs[-1][:10])
            else:
                name = list(named_src_revs)[0]
                rev  = named_src_revs[name][0]

                named_src_rev_var = 'SRCREV_{0}'.format(name)
                if d.getVar(named_src_rev_var, False):
                    d.setVar(named_src_rev_var, rev)
                else:
                    d.setVar('SRCREV', rev)

                named_src_pv_var = 'SRCPV_{0}'.format(name)
                if d.getVar(named_src_pv_var, False):
                    d.setVar(named_src_pv_var, rev[:10])
                else:
                    d.setVar('SRCPV', rev[:10])

        if src_uri != new_src_uri:
            d.setVar('SRC_URI', new_src_uri)
}

