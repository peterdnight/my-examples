#!/bin/bash

LINE="_______________________________________________________________________________________________\n"

#
# deprecated
#
function printIt() { print_with_head $* ; }
function printWithDate() { print_with_date $* ; }
function printLine() { print_line $* ; }
function printfLine() { print_columns $* ; }

#
#   prints
#
function print_with_head() { 
	echo -e "$LINE \n  $* \n$LINE"; 
}

function print_with_date() { 
	echo -e "$LINE \n `date '+%x %H:%M:%S %Nms'` host: '$HOSTNAME' user: '$USER' \n $* \n$LINE"; 
}

function print_line() { 
	echo -e "   $*" ;
}

# function test() { >&2 echo hi ; } 
function print_columns() { 
	printf "%15s: %-20s %15s: %-20s %15s: %-20s \n" "$@" ; 
}

function print_two_columns() { 
	printf "%20s: %-20s\n" "$@"; 
}

function print_if_debug() {
	
	if [ "$debug"  != "" ] ; then
		echo `date "+%Nms"`;echo -e "$*" ; echo
	fi;
}

function print_with_prompt() {
	
	print_with_head $*
	
	if $isPrompt ; then
		print_line enter to continue or ctrl-c to exit
		read progress
	fi;
}

#
# Packaging
#

function exit_if_not_installed() { 
	verify=`which $1`; 
	if [ "$verify" == "" ] ; then 
		print_with_head "error: '$1' not found, install using 'yum -y install'";
		exit; 
	fi
}

function is_process_running() { 
	
	command="$1"
	
	if (( $(ps -ef | grep -v grep | grep $command | wc -l )  > 0 ));  then 
		true ;
	else  
		false ;
	fi;   
}

function is_function_available() { 
	
	functionName="$1"
	
	if [ -n "$(type -t $functionName)" ] && [ "$(type -t $functionName)" == "function" ];  then 
		true ;
	else  
		false ;
	fi;   
}

function is_need_package() { 
	
	! is_package_installed $1
	
}

function is_package_installed () { 
	
	rpm -q $1 2>&1 >/dev/null
	
}

function is_need_command() { 
	
	! is_command_installed $1 ;
	
}

function is_command_installed() { 
	
	verify=`which $1 2>/dev/null`; 
	
	if [ "$verify" != "" ] ; then 
		#echo true
		true ;
		
	else  
		#echo false
		false ; 
		
	fi;   
	
}

function ensure_files_are_unix() {
	
	updatePath=$1
	
	if [ -f "$updatePath" ] ; then
		updatePath="$1/*" ;
	fi;
	if `is_command_installed dos2unix` ; then
		printIt "Found scripts in package, running dos2unix"
		find $updatePath -name "*.*" -exec dos2unix --quiet -n '{}' '{}' \;
	else
		printIt "Warning: did not find  dos2unix. Ensure files are linux line endings"
	fi ;
	
}

function backup_file() {
	
	originalFile="$1" ;
	backupFile="$originalFile.last" ;
	
	if [ "$2" != "" ] ; then
		backupFile="$2/`basename $backupFile`" ;
	fi;
	
	if [ -f "$originalFile" ] ; then
		mv --force	$originalFile $backupFile;
	fi ;

}

function backup_and_replace() {
	
	originalFile="$1" ;
	updatedFile="$2" ;
	
	print_line "Updating $originalFile with $updatedFile. $originalFile is being backed up"
	
	if [ -f $originalFile ] ; then 
		if [ ! -f $originalFile.orig ] ; then
			mv 	$originalFile $originalFile.orig;
		else
			mv 	$originalFile $originalFile.last;
		fi ; 
	else
		originalFolder=`dirname $originalFile`
		if [ ! -e "$originalFolder" ] ; then
			printIt "Did not find $originalFolder, creating."
			mkdir -p "$originalFolder"
		fi ;
	fi
	
	\cp -f $updatedFile $originalFile
}

function launch_background () {
	
	command="$1" ;
	arguments="$2" ;
	logFile="$3" ;
	appendLog="$4" ;
	
	backup_file $logFile $csapSavedFolder
	print_with_head "Starting: '$command' in '$(pwd)'"
	print_line "log location: '$logFile', append: '$appendLog'"
	print_line "Arguments: '$arguments'"

	# First spawn a background process to do the agent kill
	# redirect error: 2>&1  replace file if exists and noclobber set: >|
	
	if [ "$appendLog" == "appendLogs" ] ; then
		nohup $command $arguments >> $logFile 2>&1 &
		
	else
		nohup $command $arguments >| $logFile 2>&1 &
		
	fi	
	
	thePid="$!" ; theReturnCode=$? ;
	thePidFile="${logFile%.log}.pid"
	
	echo $thePid >| $thePidFile
	print_line "nohup return code: '$theReturnCode', pidFile: '$thePidFile'";
	
	# sleep is need to making sure any errors output by the nohup itself are captured in output
	sleep 1 ;

}

function add_link_in_pwd() {
	
	pathOnOs="$1"
	
	linkedPath="link"${pathOnOs////-} ;
	
	print_line "Adding link to $pathOnOs as $linkedPath"
	
	ln -s $pathOnOs $linkedPath
}

function run_using_root() {
	
	command_to_run="$*" ;
	
	print_with_head "Running using root: '$command_to_run'"
	
	rm -rf $STAGING/bin/rootDeploy.sh
	
	echo $command_to_run > $STAGING/bin/rootDeploy.sh
	
	chmod 755 $STAGING/bin/rootDeploy.sh
	sudo $STAGING/bin/rootDeploy.sh
			
}

function run_using_csap_root_file() {
	
	command_to_run="$1" ;
	command_script="$2" ;
	variable_script="$3" ;
	
	
	
	# remove first argument from $*, it will be replaced with helperFile
	shift 1 ;
	
	rm -rf $STAGING/bin/rootDeploy.sh
	
	if [ ! -f "$variable_script" ] ; then 
		print_line "Did not find variable_script: '$variable_script'" ;
		return ;
	fi;
	
	cat $variable_script > $STAGING/bin/rootDeploy.sh
	
	if [ ! -f "$command_script" ] ; then 
		print_line "Did not find command_script file: '$command_script'" ;
		return ;
	fi;
	
	cat $command_script >> $STAGING/bin/rootDeploy.sh

	
	chmod 755 $STAGING/bin/rootDeploy.sh
	sudo $STAGING/bin/rootDeploy.sh $STAGING/bin/csap-shell-utilities.sh $command_to_run
			
}

function run_using_csap_root() {
	
	rootInstallScript="$1" ;
	
	# remove first argument from $*, it will be replaced with helperFile
	shift 1 ;
	
	rm -rf $STAGING/bin/rootDeploy.sh
	
	if [ ! -f "$rootInstallScript" ] ; then 
		print_line "Did not find root install script file: '$rootInstallScript'" ;
		return ;
	fi;
	
	cat $rootInstallScript > $STAGING/bin/rootDeploy.sh
	
	chmod 755 $STAGING/bin/rootDeploy.sh
	sudo $STAGING/bin/rootDeploy.sh $STAGING/bin/csap-shell-utilities.sh $*
			
}

note_indent="   ";
function add_note() {
	
	currentNote="$*" ;
	if [ "$currentNote" == "start" ] ; then 
		add_note_contents="\n$LINE\n" ;
		
	elif [ "$currentNote" == "end" ] ; then 
		add_note_contents+="\n$LINE\n" ;
		
	else
		add_note_contents+="$currentNote\n$my_indent"
	fi;
}

function delay_message_seconds() {
	
	local delay_in_seconds=${1:-200} ;
	local max_poll_result_attempts=$(( $delay_in_seconds / 2 )) ;
	local message=${2:-Delaying execution}
	print_with_head "$message"
	local currentAttempt=1;
	for i in $(seq $currentAttempt $max_poll_result_attempts); do
	   	print_line "Delay: $(( i * 2)) of $delay_in_seconds (seconds)"
        sleep 2;
    done;
}

#
#   k8s helpers
#
function find_pod_name() {
	
	podTarget=$1
	podFullName=$(kubectl get pod --all-namespaces | grep "$podTarget" | tail -1 | awk '{print $2}')
	
	#
	# this function returns the name of the pod
	#
	echo "$podFullName"
}

function find_pod_names() {
	
	podTarget=$1
	podNames=$(kubectl get pod --all-namespaces | grep "$podTarget" | awk '{print $2}')
	
	#
	# this function returns the name of the pod
	#
	echo "$podNames"
}


function find_pod_namespace() {
	local targetPod=$1;
	kubectl get pods --all-namespaces | grep $targetPod | tail -1 | awk '{print $1}'
}

function wait_for_pod_log() {
	
	podPattern="$1"
	podLogPattern=${2:-1}
	number_of_pods=${3:-1}
	logNamespace=${4:-all}
	max_poll_result_attempts=${5:-200} ;
	tail_filter=${6:---tail=200} ;
	
	wait_for_pod_running $podPattern $number_of_pods $logNamespace $max_poll_result_attempts
	
	targetPods=$(find_pod_names $podPattern) ;
	print_with_head "Waiting for logs pattern: '$podLogPattern', pod_tail: '$tail_filter', in pods: '$targetPods' "
	
	local podCount=0 ;
	local currentAttempt=1;
	
	for targetPod in $targetPods; do
		podCount=$(( $podCount + 1)) ;
		if [ $logNamespace == "all" ] ; then
			detectedNamespace=$(find_pod_namespace $targetPod)
			namespace="--namespace=$detectedNamespace" ;
		else
			namespace="--namespace=$logNamespace";
		fi;
		for i in $(seq $currentAttempt $max_poll_result_attempts); do
	        
	        sleep 2;
	        
	        # preserve attempt total across all pods
	        currentAttempt=$(( $currentAttempt + 1 )); 
	        
	        containerNames=$(kubectl get pods $targetPod $namespace -o jsonpath='{.spec.containers[*].name}') ;
	        
	        containerMatches=0 ;
	        logsFound="" ;
	        for containerName in $containerNames; do
	        	
	        	print_if_debug "Logs container: '$containerName',  attempt $i of $max_poll_result_attempts: \n"
	        	print_if_debug "$(kubectl logs $targetPod $namespace --container=$containerName --tail=20)\n"
	        	numMatchedLogs=$(kubectl logs $targetPod $namespace --container=$containerName $tail_filter | grep "$podLogPattern" | wc -l) ;
	        	if (( $numMatchedLogs > $containerMatches )) ; then
					containerMatches=$numMatchedLogs;
				fi ;
				
	    	done
	
			print_line "attempt $i of $max_poll_result_attempts: pod $podCount: '$targetPod',  waiting for log pattern: '$podLogPattern', matches found: '$containerMatches'\n"
	    
			if (( $containerMatches > 0 )) ; then
				break ;
			fi ;
	    	
		done ;
	done ;
	
	failed_to_find_logs=false;
	if (( $currentAttempt >= $max_poll_result_attempts )) ; then
		failed_to_find_logs=true;	
	fi
    
    print_with_head "Pod pattern: '$podPattern', log pattern: '$podLogPattern', matches: '$containerMatches' "
}


function wait_for_pod_running() {
	
	local podPattern="$1"
	local number_of_pods=${2:-1}
	local namespace=${3:-all}
	local max_poll_result_attempts=${4:-200} ;
	
	if [ $namespace == "all" ] ; then
		namespace="--all-namespaces" ;
	else
		namespace="--namespace=$namespace";
	fi;
	
	print_with_head "Waiting for: '$number_of_pods' pods in run state: '$podPattern' in '$namespace'."
	for i in $(seq 1 $max_poll_result_attempts); do
        sleep 2;

		print_line "attempt $i of $max_poll_result_attempts: \n$(kubectl get pods $namespace | grep $podPattern)\n"
    
    	numPods=$(kubectl get pods $namespace | grep $podPattern | grep " Running" | wc -l)
		if (( $numPods >= $number_of_pods )) ; then
			break;
		fi ;
    	
    done
    
    print_line "Pod pattern: '$podPattern', '$numPods' in running state"
}

function is_pod_running() {
	
	podPattern="$1"
	namespace=${2:-all}
	
	if [ $namespace == "all" ] ; then
		namespace="--all-namespaces" ;
	else
		namespace="--namespace=$namespace";
	fi;
	
	numPods=$(kubectl get pods $namespace | grep $podPattern | wc -l) ;
		
	if (( $numPods == 0 )) ; then
		>&2 print_line "Not Found: '$podPattern' in '$namespace'"
		echo false ;
	else
		>&2 print_line "Found $numPods pods: '$podPattern' in '$namespace'"
		echo true ;
	fi ;
	
}

function wait_for_pod_removed() {
	
	podPattern="$1"
	namespace=${2:-all}
	max_poll_result_attempts=${3:-50} ;
	
	if [ $namespace == "all" ] ; then
		namespace="--all-namespaces" ;
	else
		namespace="--namespace=$namespace";
	fi;
	
	print_with_head "Waiting for all pods to be removed, pattern: '$podPattern' in '$namespace'."
	
	local numPods=99
	for i in $(seq 1 $max_poll_result_attempts); do
        sleep 2;

		print_line "attempt $i of $max_poll_result_attempts: \n$(kubectl get pods $namespace | grep $podPattern) \n"
		numPods=$(kubectl get pods $namespace | grep $podPattern | wc -l) ;
		
		if (( $numPods == 0 )) ; then
			break;
		fi ;
    	
    done
    
	pod_still_found=true;
	if (( $numPods == 0 )) ; then
		pod_still_found=false;	
	fi
	
    print_line "Pod pattern: '$podPattern', '$numPods' found"
}

function run_remote() {
	
	local user="$1";
	local password="$2";
	local hosts="$3";
	
	# handle array
	shift;shift;shift
	local commands=("$@");
	
	if $(is_need_package sshpass) ; then
	run_using_root yum -y install sshpass
	fi;
	
	exit_if_not_installed sshpass
	
	
	
	for targetHost in $hosts; do
	
		for command in "${commands[@]}"; do
			print_with_head	"$targetHost: running '$command'" ;
			sshpass -p $password ssh -o StrictHostKeyChecking=no $user@$targetHost $command
		done
		
	done;
	
}
