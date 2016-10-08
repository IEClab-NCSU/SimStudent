#!/bin/bash
#
#  This script is to run StepAnalysis.java
#
#  Usage: ./runStepAnalysis.sh BRD_FILES OUTPUT_FILE
#  
#  BRD_FILES can be a directory. Everything in the directory will be
#  reported recursively 
#  

echo ${OS}

# Class path
# 
if [ "${CVSDIR}" = "" ]
then
    if [ "${OS}" = "Windows_NT" ]; then
	CVSDIR="f:/Project/CTAT/CVS-TREE"
    fi
    if [ "${OS}" != "Windows_NT" ]; then
	CVSDIR="${HOME}/mazda-on-Mac/Project/CTAT/CVS-TREE"
    fi
fi

CommJar="${CVSDIR}/AuthoringTools/java/lib/CommWidgets.jar"

if [ "${OS}" = "Windows_NT" ]; then
    CPATH="${CommJar};..;."
    VmOption="-cp ${CPATH} -Xmx512m"
fi
if [ "${OS}" != "Windows_NT" ]; then
    CPATH="${CommJar}:..:."
    VmOption="-cp ${CPATH}"
fi


cmd="java ${VmOption} edu.cmu.pact.miss.BrdAnalyzer.StepAnalysis $1 $2"
echo $cmd
$cmd
