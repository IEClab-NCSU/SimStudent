#!/bin/bash
CVSDIR=$(pwd)
cd SimStAlgebraV8
ProjectDir=$(pwd)
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


VmOption="-cp ${CPATH} -Xmx512m -DssFoilBase=../FOIL6";

TutorArg="-traceLevel 3 -debugCodes miss ss"


TutorArg="${TutorArg} -ssProjectDir ${ProjectDir}"
TutorArg="${TutorArg} -br"
TutorArg="${TutorArg} -ssSearchTimeOutDuration 60000"


cmd="java ${VmOption} SimStAlgebraV8/SimStAlgebraV8 ${TutorArg}"
echo "${cmd}"
${cmd};

for bload in *.bload
do
    rm -f ./"$bload"
done
