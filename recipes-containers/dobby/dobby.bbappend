#Skip the installation of device settings file in middleware layer
EXTRA_OECMAKE:append = " -DSETTINGS_FILE=DONT_INSTALL_DEVICESETTINGSFILE "

# Enable Hibernate Memcr implementation
EXTRA_OECMAKE += "${@bb.utils.contains('DISTRO_FEATURES', 'RDKTV_APP_HIBERNATE', ' -DDOBBY_HIBERNATE_MEMCR_IMPL=ON -DDOBBY_HIBERNATE_MEMCR_PARAMS_ENABLED=ON','',d)}"

# Enable plugins
PACKAGECONFIG:append = " gpu localtime rtscheduling httpproxy ionmemory"
