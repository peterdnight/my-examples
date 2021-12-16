#!/bin/bash


source shell-utilities.sh

#
# find and remove spaces
#

photosFolder="$HOME/google-drive/family-photos/" ;

#ls
if test -d  $photosFolder ; then

	cd $photosFolder

	
	find . -depth -name '* *' \
		| while IFS= read -r fullPathToFile ; do \
			cp -p  -v "$fullPathToFile" "$(dirname "$fullPathToFile")/$(basename "$fullPathToFile"|tr -d ' ' )" ; \
		done

	
	find . -depth -name '* *' \
		| while IFS= read -r fullPathToFile ; do \
			rm -f -v "$fullPathToFile"  ; \
		done
		


else 
	echo no folder $photosFolder
	
fi ;






