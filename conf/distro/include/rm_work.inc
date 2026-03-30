#Enable the rm_work class so the build system automatically removes per-recipe work directories once they are no longer needed (typically after the recipe has successfully produced packages).
#Key Risk / Developer Impact : Enabling rm_work can delete recipe work directories that developers sometimes use for:

#	editing sources directly under ${TMPDIR}/work/...
#	manual debugging of build output
#	post-failure investigation using intermediate artifacts

#Risk: If developers modify/build source code directly in a recipe’s work directory, those changes can be lost when rm_work cleans that recipe.

#Mitigation / Exclusions : Exclude selected recipes from cleanup using RM_WORK_EXCLUDE in local.conf (e.g., recipes actively being developed/debugged):
#example: RM_WORK_EXCLUDE += "busybox glibc"
#This allows active development/debug recipes to retain their work directories while the rest of the build benefits from cleanup.


INHERIT += "rm_work"
#Excluding rm_work for middleware-test-image as some oem tasks might need the middleware-test-image's packages after do_rootfs.
RM_WORK_EXCLUDE += "middleware-test-image"
