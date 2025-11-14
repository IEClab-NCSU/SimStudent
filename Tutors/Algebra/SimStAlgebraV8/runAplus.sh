#!/bin/bash

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
"-ct") AddArgs="${AddArgs} -ssCogTutorMode";;
"-cta") AddArgs="${AddArgs} -ssAplusCtrlCogTutorMode";;
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
echo "-mtc - turns metatutor on with cognitive hint only"
echo "-mtmc - turns metatutor on with meta-cognitive hint only"
echo "-cta - launch APLUS in AplusControl mode"
echo "-ct - launch APLUS in Cognitive Tutor mode"
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


# VmOption="-cp ${CPATH} -DnoCtatWindow -Xmx512m"
if [ -z $br ];
then
VmOption="-cp ${CPATH} -Xmx512m -DssFoilBase=../FOIL6 -XX:+UnlockCommercialFeatures -XX:+FlightRecorder";
else
VmOption="-cp ${CPATH} -Xmx512m -DssFoilBase=../FOIL6"
fi
# CTAT options

TutorArg="-traceLevel 3 -debugCodes miss cogTutor ss sswme sslist"
#TutorArg="-traceLevel 3 -debugCodes mt1"
if [ -z ${PLE} ];
then
	TutorArg="${TutorArg} -ssRunInPLE ";
fi
if [ -z ${SE} ];
then
	TutorArg="${TutorArg} -ssSelfExplainMode";
fi


if [ $1 == "-ct" ]; 
then
TutorArg="${TutorArg} -ssHintMethod builtInClSolverTutor";
TutorArg="${TutorArg} -ssRuleActivationTestMethod builtInClSolverTutor";
else
if [ $1 == "-cta" ]; 
then
TutorArg="${TutorArg} -ssHintMethod builtInClSolverTutor";
TutorArg="${TutorArg} -ssRuleActivationTestMethod builtInClSolverTutor";
else
TutorArg="${TutorArg} -ssRuleActivationTestMethod humanOracle";
TutorArg="${TutorArg} -ssHintMethod humanDemonstration";
fi
fi


if [ $1 == "-mtc" ]; 
then
TutorArg="${TutorArg} -ssMetaTutorMode";
TutorArg="${TutorArg} -ssMetaTutorModeLevel Cognitive";
else
if [ $1 == "-mtmc" ]; 
then
TutorArg="${TutorArg} -ssMetaTutorMode";
TutorArg="${TutorArg} -ssMetaTutorModeLevel MetaCognitive";
fi
fi


#TutorArg="${TutorArg} -ssIntroVideo metatutor.mov"
#TutorArg="${TutorArg} -ssIntroVideo aplus_controlS7.mov"
TutorArg="${TutorArg} -ssIntroVideo cogTutor.mov"
TutorArg="${TutorArg} -ssOverviewPage curriculum.html"
TutorArg="${TutorArg} -ssLoadPrefsFile brPrefsStacy.xml"
TutorArg="${TutorArg} -ssCacheOracleInquiry false"
TutorArg="${TutorArg} -ssLocalLogging true"
#TutorArg="${TutorArg} -ssMetaTutorModeLevel Cognitive"
#TutorArg="${TutorArg} -ssMetaTutorModeLevel MetaCognitive"
#TutorArg="${TutorArg} -ssLogging true"
#TutorArg="${TutorArg} -ssLogURL http://pslc-qa.andrew.cmu.edu/log/server"
TutorArg="${TutorArg} -ssSearchTimeOutDuration 20000"
TutorArg="${TutorArg} -ssTutorServerTimeOutDuration 100000"
TutorArg="${TutorArg} -ssMaxSearchDepth 3"
TutorArg="${TutorArg} -ssSkillNameGetterClass SimStAlgebraV8.AlgebraV8AdhocSkillNameGetter"
TutorArg="${TutorArg} -ssSetInactiveInterfaceTimeout 5000000"
#TutorArg="${TutorArg} -ssRuleActivationTestMethod humanOracle"
#TutorArg="${TutorArg} -ssHintMethod humanDemonstration"
TutorArg="${TutorArg} -ssInputCheckerClass SimStAlgebraV8.AlgebraV8InputChecker"
TutorArg="${TutorArg} -ssStartStateCheckerClass SimStAlgebraV8.AlgebraV8StartStateChecker"
TutorArg="${TutorArg} -ssFoaGetterClass SimStAlgebraV8.AlgebraV8AdhocFoaGetter"
TutorArg="${TutorArg} -ssStepNameGetterClass SimStAlgebraV8.AlgebraV8AdhocStepNameGetter"
TutorArg="${TutorArg} -ssSelectionOrderGetterClass SimStAlgebraV8.AlgebraV8AdhocSelectionGetter"
TutorArg="${TutorArg} -ssClSolverTutorSAIConverter SimStAlgebraV8.AlgebraV8AdhocSAIConverter"
TutorArg="${TutorArg} -ssActivationList AccuracySortedActivationList"
TutorArg="${TutorArg} -ssFoaClickDisabled"
TutorArg="${TutorArg} -ssProjectDir ."
TutorArg="${TutorArg} -ssNumBadInputRetries 2"
TutorArg="${TutorArg} -ssProblemsPerQuizSection 2"
TutorArg="${TutorArg} -Dcourse_name=SimStudent_StudyVII_Preparation"
TutorArg="${TutorArg} -Dschool_name=someSchool"
TutorArg="${TutorArg} -Dclass_name=someClass"
TutorArg="${TutorArg} -ssCondition devTesting"
TutorArg="${TutorArg} ${AddArgs}"

cmd="java ${VmOption} SimStAlgebraV8/SimStAlgebraV8 ${TutorArg}"
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
