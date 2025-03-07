# Deprecated. Please use srcrev-override.bbclass

python () {
    src_uri = d.getVar('SRC_URI', True)
    repos_host = d.getVar('REPOS_HOST', False)

    if not repos_host:
        return

    if repos_host in src_uri:
        code_reviews_val = d.getVar('CODE_REVIEWS', False)
        if not code_reviews_val:
            return

        code_reviews = []
        for code_review_val in code_reviews_val.split(','):
            repo_name, change_rev = code_review_val.split()

            code_reviews.append((repo_name, change_rev))

        new_src_uri = src_uri
        urls = new_src_uri.split()

        for url in urls:
            fetch_type, host, path, usr, paswd, params = bb.fetch.decodeurl(url)

            src_rev_var = 'SRCREV'
            src_pv_var  = 'SRCPV'

            if 'name' in params:
               src_rev_var += '_' + params['name']
               src_pv_var  += '_' + params['name']

            for repo_name, change_rev in code_reviews:
               if not repo_name == path.strip('/'):
                  continue

               params.pop('branch', None)
               params['rev'] = change_rev
               params['nobranch'] = "1"

               new_url = bb.fetch.encodeurl((fetch_type, host, path, usr, paswd, params))
               new_src_uri = new_src_uri.replace(url, new_url)

               d.setVar(src_rev_var, change_rev)
               d.setVar(src_pv_var, change_rev[:10])

        if src_uri != new_src_uri:
            d.setVar('SRC_URI', new_src_uri)
}
