python () {
    license = d.getVar('LICENSE', True)
    if license in {'CLOSED', 'PROPRIETARY'}:
        return
    import sys
    if sys.version_info >= (3, 0):
        from urllib.parse import urlparse
    else:
        from urlparse import urlparse
    component = d.getVar('PN', True)
    source = d.getVar('SRC_URI', True)
    version = d.getVar('PV', True)
    homepage = d.getVar('HOMEPAGE', True)

    try:
        uri = source.split()[0]
        uri = uri.split(';')[0]
    except IndexError:
        uri = 'No SRC_URI'

    if 'file://' in uri:
        uri = 'No SRC_URI'

    if homepage:
        supplier = urlparse(homepage).netloc
    else:
        supplier = urlparse(uri).netloc
    st = '{comp: <30}|{version: <33}|{supplier: <30}|{source: >3}|Open Source |{lic: <40}\n'
    st = st.format(comp=component, version=version, supplier=supplier, source=uri, lic=license)
    with open('open_source_report.txt', 'a') as fw:
        fw.write(st)
}
