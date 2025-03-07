python() {
       src_uri = d.getVar('SRC_URI', True)
       pn = d.getVar('FILE', True)
       with open('src_uri_map.txt','a') as artif:
         artif.write(pn)
         artif.write(':')
         artif.write(src_uri)
         artif.write('\n')

}
