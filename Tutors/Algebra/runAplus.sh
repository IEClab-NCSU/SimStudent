#!/bin/bash
CVSDIR=$(pwd)
cd SimStAlgebraV8
ProjectDir=$(pwd)
loggingOptions=""
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

redir="off"

case $1 in
	"-ct") AddArgs="${AddArgs} -ssCogTutorMode  -ssHintMethod builtInClSolverTutor -ssRuleActivationTestMethod builtInClSolverTutor -ssIntroVideo cogTutor.mov -ssCondition CogTutorControl";;
	"-cta") AddArgs="${AddArgs} -ssAplusCtrlCogTutorMode -ssProblemCheckerOracle ClOracle -ssHintMethod builtInClSolverTutor -ssRuleActivationTestMethod builtInClSolverTutor -ssIntroVideo aplus_controlS7.mov  -ssCondition AplusControl";;
	"-mt") AddArgs="${AddArgs} -ssMetaTutorMode -ssProblemCheckerOracle ClOracle -ssRuleActivationTestMethod humanOracle -ssHintMethod humanDemonstration -ssIntroVideo metatutor.mov  -ssCondition MetaTutor";;
	"-mtc") AddArgs="${AddArgs} -ssMetaTutorMode -ssProblemCheckerOracle ClOracle -ssMetaTutorModeLevel Cognitive -ssRuleActivationTestMethod humanOracle -ssHintMethod humanDemonstration -ssIntroVideo metatutorC.mov -ssCondition MetaTutorC";;
	"-mtmc") AddArgs="${AddArgs} -ssMetaTutorMode -ssProblemCheckerOracle ClOracle -ssMetaTutorModeLevel MetaCognitive -ssRuleActivationTestMethod humanOracle -ssHintMethod humanDemonstration -ssIntroVideo metatutorMC.mp4 -ssCondition MetaTutorMC";;
  "-cti") AddArgs="${AddArgs} -ssConstructiveTuteeInquiryFTIMode -ssMetaTutorMode -ssProblemCheckerOracle ClOracle -ssMetaTutorModeLevel MetaCognitive -ssRuleActivationTestMethod humanOracle -ssHintMethod humanDemonstration -ssIntroVideo metatutorMC.mp4 -ssCondition MetaTutorMC";;
  "-h"|"-help"|*) echo "Usage: $0"
	echo " ./runAplus.sh <condition>[-mt | -mtc | -mtmc | -cta | -ct] <otheroptions>"
	echo "	-datashopLogging <coursename> - turns logging to datashop on"
	echo "	-localLogging <coursename>    - turns local logging on"
	echo "	-noLogging - turns off logging"
	#echo "	-noPle - turns PLE off"
	echo "	-noSe - turn Self Explanation off"
	echo "	-mt - turns metatutor on"
	echo "	-mtc - turns metatutor on with cognitive hint only"
	echo "	-mtmc - turns metatutor on with meta-cognitive hint only"
	echo "	-cta - launch APLUS in AplusControl mode"
	echo "	-ct - launch APLUS in Cognitive Tutor mode"
	echo "	-tt - turns Tutalk on"
  echo "	-cti - turns constructive tutee inquiry on"
	echo "	-br - displays Behavior Recorder window"
	echo "	-u or -user <name> - sets the user ID"
	echo "	-o or -output <filename> - redirects output to file"
	exit;;
esac


for i do
case $i in
"-noPle") PLE="off";;
"-noSe") SE="off";;
"-tt") AddArgs="${AddArgs} -ssTutalkParams none";;
"-cti") AddArgs="${AddArgs} -ssConstructiveTuteeInquiryFTIMode -ssCTIBothStuckParams none";;
"-br") br="on";;
"-u"|"-user") AddArgs="${AddArgs} -ssUserID";;
"-o"|"-output") redir="on";;
"-noLogging") AddArgs="${AddArgs} -ssNoLogging";;
"-datashopLogging") AddArgs="${AddArgs} -ssLogging"
loggingOptions="-datashopLogging";;
"-localLogging") AddArgs="${AddArgs} -ssLocalLogging"
loggingOptions="-localLogging";;
*)
if [[ ! ${i} ]] || [[ ${i} != -* ]];
then
    if [[ -z ${outstr} ]] && [[ ${redir} == "on" ]];
	then
		outstr="$i";
    elif [ "${loggingOptions}" == "-datashopLogging" ];
    then
        AddArgs="${AddArgs} -ssLogURL http://pslc-qa.andrew.cmu.edu/log/server";
		AddArgs="${AddArgs} -Dcourse_name=${i}";
		loggingOptions="";
	elif [ "${loggingOptions}" == "-localLogging" ];
	then
		AddArgs="${AddArgs} -Dcourse_name=${i}";
		loggingOptions="";
	else
	    AddArgs="${AddArgs} ${i}";
	fi;
fi;;
esac
done

if	[[ "${AddArgs}" != *-ssLocalLogging* ]] && [[ "${AddArgs}" != *-ssLogging* ]] && [[ "${AddArgs}" != *-ssNoLogging* ]];
then
    echo "Provide any one of the logging options";
    echo "	-datashopLogging <coursename> - turns logging to datashop on";
	echo "	-localLogging <coursename>    - turns local logging on";
	echo "	-noLogging - turns off logging";
   #echo "	-noPle - turns PLE off";
	echo "	-noSe - turn Self Explanation off"
	echo "	-mt - turns metatutor on"
	echo "	-mtc - turns metatutor on with cognitive hint only"
	echo "	-mtmc - turns metatutor on with meta-cognitive hint only"
	echo "	-cta - launch APLUS in AplusControl mode"
	echo "	-ct - launch APLUS in Cognitive Tutor mode"
	echo "	-tt - turns Tutalk on"
	echo "	-br - displays Behavior Recorder window"
	echo "	-u or -user <name> - sets the user ID"
	echo "	-o or -output <filename> - redirects output to file"
    exit 1;
fi

if [[ "${AddArgs}" != *-Dcourse_name* ]] && [[ "${AddArgs}" != *-ssNoLogging* ]];
then
	echo "Provide a valid dataset name for the logging";
	exit 1;
fi

echo "COMMAND LINE ARGUMENTS : ${AddArgs}";

# VmOption="-cp ${CPATH} -DnoCtatWindow -Xmx512m"
#if [ -z $br ];
#then
#VmOption="-cp ${CPATH} -Xmx512m -DssFoilBase=../FOIL6 -XX:+UnlockCommercialFeatures -XX:+FlightRecorder";
#else
VmOption="-cp ${CPATH} -Xmx512m -DssFoilBase=../FOIL6 -DappRunType=shellscript"
#fi
# CTAT options
TutorArg="-traceLevel 3"
if [ -z ${PLE} ];
then
	TutorArg="${TutorArg} -ssRunInPLE ";
fi
if [ -z ${SE} ];
then
	TutorArg="${TutorArg} -ssSelfExplainMode";
fi




TutorArg="${TutorArg} -ssProjectDirectory ${ProjectDir}"
TutorArg="${TutorArg} -ssOverviewPage curriculum.html"
TutorArg="${TutorArg} -ssLoadPrefsFile brPrefsStacy.xml"
TutorArg="${TutorArg} -sslogFolder log/APLUSTER"
TutorArg="${TutorArg} -ssCacheOracleInquiry false"
TutorArg="${TutorArg} -ssSearchTimeOutDuration 20000"
TutorArg="${TutorArg} -ssTutorServerTimeOutDuration 100000"
TutorArg="${TutorArg} -ssMaxSearchDepth 3"
TutorArg="${TutorArg} -ssSkillNameGetterClass SimStAlgebraV8.AlgebraV8AdhocSkillNameGetter"
TutorArg="${TutorArg} -ssSkillNameGetterClass SimStAlgebraV8.NearSimilarEquationFinder"
TutorArg="${TutorArg} -ssSetInactiveInterfaceTimeout 5000000"
TutorArg="${TutorArg} -ssInputCheckerClass SimStAlgebraV8.AlgebraV8InputChecker"
TutorArg="${TutorArg} -ssStartStateCheckerClass SimStAlgebraV8.AlgebraV8StartStateChecker"
TutorArg="${TutorArg} -ssFoaGetterClass SimStAlgebraV8.AlgebraV8AdhocFoaGetter"
TutorArg="${TutorArg} -ssSelectionOrderGetterClass SimStAlgebraV8.AlgebraV8AdhocSelectionGetter"
TutorArg="${TutorArg} -ssClSolverTutorSAIConverter SimStAlgebraV8.AlgebraV8AdhocSAIConverter"
TutorArg="${TutorArg} -ssActivationList AccuracySortedActivationList"
TutorArg="${TutorArg} -ssFoaClickDisabled"
TutorArg="${TutorArg} -ssNumBadInputRetries 2"
TutorArg="${TutorArg} -ssProblemsPerQuizSection 2"
TutorArg="${TutorArg} -Dschool_name=someSchool"
TutorArg="${TutorArg} -Dclass_name=someClass"
#TutorArg="${TutorArg} -ssCondition devTesting"
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

#for bload in *.bload
#do
#    rm -f ./"$bload"
#done
