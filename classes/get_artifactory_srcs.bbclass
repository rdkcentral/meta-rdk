python () {
        src_uri = d.getVar('SRC_URI', True)
        if 'artifactory' in src_uri:
                with open('artifactory_pkgs','a') as artif:
                        src = src_uri.split()
                        for i in src:
                                if 'artifactory' in i:
                                        artif.write(i.split(';')[0].split('/')[-1])
                                        artif.write('\n')

}
