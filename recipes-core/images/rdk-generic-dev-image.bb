SUMMARY = "RDK mediaserver-client hybrid image"

IMAGE_FEATURES += "splash mediaserver mediaclient"

require rdk-generic-media-common.inc


IMAGE_INSTALL += " \
 	packagegroup-rdk-dev-test \
 	"

