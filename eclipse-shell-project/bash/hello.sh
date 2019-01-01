#!/bin/bash

echo "hi - current folder: $(pwd)"

if [ -e shell-utilities.sh ] ; then
	source shell-utilities.sh
else
	eclipseDefaultPath="bash/shell-utilities.sh" ;
	echo "Attempting to load utilities using: '$eclipseDefaultPath'"
	source $eclipseDefaultPath
	print_with_head "Warning - did not find shell-utilities in current folder"
	print_line "Recommendation: Update eclipse shell launch configuration, or ignore warning - and be aware of eclipse default run paths, or update "
fi; 
#ls

print_with_head "Current PATH environment variable"
print_line $PATH