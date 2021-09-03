#
#  Install eclipse powershell editor
#


#$console = $host.UI.RawUI
#$console.ForegroundColor = "black"
# $console.BackgroundColor = "#F7F6B2"

$Global:Admin=''
$CurrentUser = [System.Security.Principal.WindowsIdentity]::GetCurrent()
$principal = new-object System.Security.principal.windowsprincipal($CurrentUser)
if ($principal.IsInRole("Administrators")) { 
	$Admin='admin@'
} else {
	$Admin=''
}

$host.ui.rawui.WindowTitle="PowerShell: $Admin " + $CurrentUser.name

Function prompt { 
	$Admin + "$(get-location)> " 
}
							  

Function invert-consolecolors {
 
    $oldForeground = $host.ui.rawui.ForegroundColor 
    $oldBackground = $host.ui.rawui.BackgroundColor
	
	
   $oldForeground = "Gray"
   $oldBackground = "blue"
	
    $host.ui.rawui.ForegroundColor = $oldBackground
    $host.ui.rawui.BackgroundColor = $oldForeground
	
	#echo "setting host.ui.rawui.BackgroundColor to: $host.ui.rawui.BackgroundColor"
 
    Set-PSReadlineOption -ContinuationPromptBackgroundColor $oldForeground
    Set-PSReadlineOption -Token None -BackgroundColor $oldForeground
    Set-PSReadlineOption -Token Comment -BackgroundColor $oldForeground
    Set-PSReadlineOption -Token Keyword -BackgroundColor $oldForeground
    Set-PSReadlineOption -Token String -BackgroundColor $oldForeground
    Set-PSReadlineOption -Token Operator -BackgroundColor $oldForeground
    Set-PSReadlineOption -Token Variable -BackgroundColor $oldForeground
    Set-PSReadlineOption -Token Command -BackgroundColor $oldForeground
    Set-PSReadlineOption -Token Parameter -BackgroundColor $oldForeground
    Set-PSReadlineOption -Token Type -BackgroundColor $oldForeground
    Set-PSReadlineOption -Token Number -BackgroundColor $oldForeground
    Set-PSReadlineOption -Token Member -BackgroundColor $oldForeground
    Set-PSReadlineOption -EmphasisBackgroundColor $oldForeground
    Set-PSReadlineOption -ErrorBackgroundColor $oldForeground
 
 
    Set-PSReadlineOption -ContinuationPromptForegroundColor $oldBackground
    Set-PSReadlineOption -Token None -ForegroundColor $oldBackground
    Set-PSReadlineOption -Token Command -ForegroundColor "Blue"
    Set-PSReadlineOption -Token Type -ForegroundColor "DarkGray"
    Set-PSReadlineOption -Token Number -ForegroundColor "DarkGreen"
    Set-PSReadlineOption -Token Member -ForegroundColor "DarkGreen"
    Set-PSReadlineOption -Token Variable -ForegroundColor "DarkGray"
     
    $Host.PrivateData.ErrorBackgroundColor = "Yellow"
 
    #cls
}
#invert-consolecolors
 
Function reset-consolecolors {
     
	$Host.UI.RawUI.BackgroundColor = ($bckgrnd = 'Gray')
	$Host.UI.RawUI.ForegroundColor = 'White'
	$Host.PrivateData.ErrorForegroundColor = 'DarkRed'
	$Host.PrivateData.ErrorBackgroundColor = $bckgrnd
	$Host.PrivateData.WarningForegroundColor = 'Yellow'
	$Host.PrivateData.WarningBackgroundColor = $bckgrnd
	$Host.PrivateData.DebugForegroundColor = 'Yellow'
	$Host.PrivateData.DebugBackgroundColor = $bckgrnd
	$Host.PrivateData.VerboseForegroundColor = 'Green'
	$Host.PrivateData.VerboseBackgroundColor = $bckgrnd
	$Host.PrivateData.ProgressForegroundColor = 'Blue'
	$Host.PrivateData.ProgressBackgroundColor = $bckgrnd
	Clear-Host
 
}
reset-consolecolors



function s {
	& $profile
}

 

function which {
	where.exe $args
}

function show {

	$p=$($args[0])
	#echo "args Count: $($args.Count) ,  first: $p"

	if ( $($args.Count) -eq 0 ) { 
		#echo "no args" 
		Get-Childitem env:
	} else {
		#Get-Childitem env:$args
		#echo $env:$args[0]
		#$result=Get-Item Env:$p
		#echo $result
		$var='$env:' + $p
		echo $ExecutionContext.InvokeCommand.ExpandString($var)
	}
	
}


function du ($dir="$pwd") { 

	echo "One moment while disk is scanned: '$dir'. Empty folders ommitted..."
#	foreach ($o in gci) {
#	$colItems = (Get-ChildItem $o -recurse | Measure-Object -property length -sum)
#	"{0:N2}" -f ($colItems.sum / 1MB) + " MB"
#	}

	get-childitem $dir | 
		%{$f=$_; gci -r $_.FullName | 
		where { (gci $_.fullName).count -ne 0 }|
		measure-object -property length -sum |
		select  @{Name="Name"; Expression={$f}}, 
				@{Name="Sum (MB)"; 
				Expression={"{0:N3}" -f ($_.sum / 1MB) }}, Sum } |
	  sort Sum -desc |
	  format-table -Property Name,"Sum (MB)", Sum -autosize
	  
}



#aliases 
function npp { & "C:\Program Files (x86)\Notepad++\notepad++"  $args }
function dps { docker ps $args }
function dl { docker logs $args }
function dlf { docker logs --follow $args }
function dps { docker ps $args }
function d { docker $args }
function di { docker images $args }
function dr { docker run $args }
function ds { docker save -o $args }
function rund { docker run -d -p 80:8080 --rm --name ref peterdnight/boot-reference }
function rund { docker run -d  -p 80:8080 --rm --name simple peterdnight/boot-reference }
function runit { docker run -it  $args }
function runit1 { docker run -it  peterdnight/boot-reference }
#function dref { docker run -d -p 80:8080 --name ref peterdnight/boot-reference }

function dclean { 
	$currentContainers = ( docker ps -aq ) ;
	 docker stop $currentContainers ;
	 docker rm $currentContainers ;
}


#function tt { .$profile }
#set-alias clear clear-host

$ChocolateyProfile = "$env:ChocolateyInstall\helpers\chocolateyProfile.psm1"
if (Test-Path($ChocolateyProfile)) {
  Import-Module "$ChocolateyProfile"
}

$userdir = "$env:USERPROFILE"

if ( "$userdir" -ne "" -And ( Test-Path( $userdir)) ) {
  Set-Location $env:USERPROFILE
} else {
	echo "Warning - did not find environment variable: env:USERPROFILE"
}

#$env:Path += ";C:\Program Files\GnuWin32\bin"


#Import-Module posh-docker


