#!/bin/bash
CVSDIR=$(pwd)
cd SimStAlgebraV8

#if [ "${CVSDIR}" = "" ]
#then
    if [ "${OS}" = "Windows_NT" ]; then
#	CVSDIR="c:/pact-cvs-tree"
	CPS=";"
    fi
    if [ "${OS}" != "Windows_NT" ]; then
#	CVSDIR="${HOME}/Desktop/SimStudentGithub/SimStudent-master"
	CPS=":"
    fi
#fi

CtatJar="${CVSDIR}/lib/ctat.jar${CPS}${CVSDIR}/lib/jess.jar"

if [ "${OS}" = "Windows_NT" ]; then
    CPATH="${CtatJar};..;."
fi
if [ "${OS}" != "Windows_NT" ]; then
    CPATH="${CtatJar}:..:."
fi

VmOption="-cp ${CPATH} -Xmx268435456  -Xss512K "-Dinstall4j.launcherId\=18" -Dinstall4j.swt\=false";
TutorArg="-traceLevel 5 -debugCodes br"
cmd="java ${VmOption} edu/cmu/pact/BehaviorRecorder/Controller/CTAT_Launcher ${TutorArg}"
echo "${cmd}"
${cmd};


for bload in *.bload
do
    rm -f ./"$bload"
done
