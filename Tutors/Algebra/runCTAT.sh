#!/bin/bash
cd SimStAlgebraV8

if [ "${CVSDIR}" = "" ]
then
    if [ "${OS}" = "Windows_NT" ]; then
	CVSDIR="c:/pact-cvs-tree"
	CPS=";"
    fi
    if [ "${OS}" != "Windows_NT" ]; then
	CVSDIR="${HOME}/Desktop/SimStudentGithub/SimStudent-master"
	CPS=":"
    fi
fi

#CtatJar="../lib/ctat.jar${CPS}../lib/jess.jar"
CtatJar="${CVSDIR}/APLUS/java/lib/ctat.jar${CPS}${CVSDIR}/APLUS/java/lib/jess.jar"



if [ "${OS}" = "Windows_NT" ]; then
    CPATH="${CtatJar};..;."
fi
if [ "${OS}" != "Windows_NT" ]; then
    CPATH="${CtatJar}:..:."
fi

redir="off"

for i do
case $i in
"-noPle") PLE="off";;
"-noSe") SE="off";;
"-log") AddArgs="${AddArgs} -ssLogging";;
"-local") AddArgs="${AddArgs} -ssLocalLogging";;
"-mt") AddArgs="${AddArgs} -ssMetaTutorMode";;
"-tt") AddArgs="${AddArgs} -ssTutalkParams none";;
"-br") br="on";;
"-u"|"-user") AddArgs="${AddArgs} -ssUserID";;
"-o"|"-output") redir="on";;
"-h"|"-help") echo "Usage: $0"
echo "-noPle - turns PLE off"
echo "-noSe - turn Self Explanation off"
echo "-log - turns logging to datashop on"
echo "-local - turns local logging on"
echo "-mt - turns metatutor on"
echo "-tt - turns Tutalk on"
echo "-br - displays Behavior Recorder window"
echo "-u or -user <name> - sets the user ID"
echo "-o or -output <filename> - redirects output to file"
exit;;
*) 
if [ ${redir} == "on" ];
then
	if [ -z ${outstr} ];
	then
		outstr="$i";
	else
		AddArgs="${AddArgs} $i";
	fi;
else
	AddArgs="${AddArgs} $i";
fi;;
esac
done

VmOption="-cp ${CPATH} -Xmx268435456  -Xss512K "-Dinstall4j.launcherId\=18" -Dinstall4j.swt\=false";
# VmOption="-cp ${CPATH} -DnoCtatWindow -Xmx512m"
#if [ -z $br ];
#then
#VmOption="-cp ${CPATH} -Xmx512m -DssFoilBase=../FOIL6";
#else
#VmOption="-cp ${CPATH} -Xmx512m -DssFoilBase=../FOIL6"
#fi
# CTAT options

TutorArg="-traceLevel 5 -debugCodes br"
#if [ -z ${PLE} ];
#then
#	TutorArg="${TutorArg} -ssRunInPLE";
#fi
#if [ -z ${SE} ];
#then
#	TutorArg="${TutorArg} -ssSelfExplainMode";
#fi

#TutorArg="-traceLevel 5 -debugCodes miss ss"
#TutorArg="${TutorArg} -ssProjectDir ."
#TutorArg="${TutorArg} -br"
#TutorArg="${TutorArg} -ssSearchTimeOutDuration 60000"
#TutorArg="${TutorArg} -Dcourse_name=simStAlgebraStacy"
#TutorArg="${TutorArg} -Dschool_name=testSchool"
#TutorArg="${TutorArg} -Dclass_name=testClass"
#TutorArg="${TutorArg} -ssCondition devTesting"

cmd="java ${VmOption} edu/cmu/pact/BehaviorRecorder/Controller/CTAT_Launcher ${TutorArg}"
if [ ${redir} == "on" ];
then
	echo "${cmd} &>${outstr}"
	${cmd} &>${outstr};
else
	echo "${cmd}"
	${cmd};
fi

for bload in *.bload
do
    rm -f ./"$bload"
done
