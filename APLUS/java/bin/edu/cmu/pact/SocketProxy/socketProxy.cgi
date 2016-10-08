#!/bin/bash

this="$0"

# Start the Behavior Recorder's SocketProxy interface for communication
# with Flash.

# Default argument values

brMode="Pseudo-Tutor"

#####################################################
# Print an error message and exit with error code 1
# arg1 is message to print
errorExit() {
	printf "\nError in %s:\n   %s.\n\n" "$this" "$1"
	exit 1
}

# Set execution environment variables for Java VM, classpath, etc.
#
jvm=$(which java 2>/dev/null)
if test -z "$jvm"; then
	errorExit "no Java Virtual Machine available"
fi

libDir="lib"
if ! test -d "$libDir"; then
	errorExit "library directory \"$libDir\" not found"
fi

pathSep=";"   # change to colon (":") on Unix

typeset -a jarList="
	$libDir/CommWidgets.jar
	$libDir/jmf.jar
	$libDir/cl/utilities.jar
	$libDir/cl/LMS.jar
	$libDir/cl/cl_common.jar
	$libDir/jess.jar
	$libDir/jdom.jar
	$libDir/jnlp.jar
	$libDir/AbsoluteLayout.jar"
#echo ";"${jarList[*]}";"
jars=$(echo ${jarList[*]} | sed "s/ /${pathSep}/g")
#echo "!"${jars}"!"

mainClass="edu.cmu.pact.SocketProxy.SocketProxy"

# Process command-line arguments.

brdFilesRoot="deploy-tutor/Projects.jar"
brdFile=""
if test $# -le 0; then
	errorExit "problem file (.brd) argument is required"
else
	brdFile="$1"
	if test -d "${brdFilesRoot}"; then
		if ! test -f "${brdFilesRoot}/${brdFile}"; then
			errorExit "problem file \"${brdFilesRoot}/$brdFile\" not found"
		else
			brdFile="${brdFilesRoot}/${brdFile}"
		fi
		if ! test -r "${brdFile}"; then
			errorExit "problem file \"$brdFile\" not readable"
		fi
	fi
fi

if test $# -gt 1; then
	brMode="$2"
	match=$(echo "$brMode" | grep -E -ic '^Pseudo-Tutor|Tutor$')
	if test "$match" -lt 1; then
		errorExit "bad BR mode argument \"$brMode\""
	fi
fi

# Initialization parameters as Java System properties
# FIXME:  handle spaces in school, course, etc.
typeset -a properties="
	-Dschool_name=__SCHOOL_NAME__
	-Dcourse_name=__COURSE_NAME__
	-Dunit_name=__UNIT_NAME__
	-Dsection_name=__SECTION_NAME__
	-DProblemFileLocation=${brdFile}
	-DBehaviorRecorderMode=${brMode}
	-DBehaviorRecorderVisible=true"

set -x
exec $jvm -cp "${brdFilesRoot}${pathSep}${jars}" ${properties[*]} $mainClass -d -e 0A -b -m -M