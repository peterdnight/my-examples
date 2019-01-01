
function print_header { 
	$line="_____________________________________________________________________`n" ;
	write-output "$line `n $args `n$line"
}


print_header "Current folder is $pwd"

print_header "file listing: using ps1 ls"
ls

print_header "file listing: using ps1 native syntax"
get-childitem -name  | sort LastWriteTime -Descending

#print_header "process listing: sorted by id"
#Get-Process | Sort-Object id