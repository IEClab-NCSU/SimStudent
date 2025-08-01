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
condition=""
case $1 in
	#"-ct") AddArgs="${AddArgs} -ssCogTutorMode  -ssHintMethod builtInClSolverTutor -ssRuleActivationTestMethod builtInClSolverTutor -ssIntroVideo cogTutor.mov -ssCondition CogTutorControl";;
	#"-cta") AddArgs="${AddArgs} -ssAplusCtrlCogTutorMode -ssProblemCheckerOracle ClOracle -ssHintMethod builtInClSolverTutor -ssRuleActivationTestMethod builtInClSolverTutor -ssIntroVideo aplus_controlS7.mov  -ssCondition AplusControl";;
	#"-mt") AddArgs="${AddArgs} -ssMetaTutorMode -ssProblemCheckerOracle ClOracle -ssRuleActivationTestMethod humanOracle -ssHintMethod humanDemonstration -ssIntroVideo metatutor.mov  -ssCondition MetaTutor";;
	#"-mtc") AddArgs="${AddArgs} -ssMetaTutorMode -ssProblemCheckerOracle ClOracle -ssMetaTutorModeLevel Cognitive -ssRuleActivationTestMethod humanOracle -ssHintMethod humanDemonstration -ssIntroVideo metatutorC.mov -ssCondition MetaTutorC";;
	#"-mtmc") AddArgs="${AddArgs} -ssMetaTutorMode -ssProblemCheckerOracle ClOracle -ssMetaTutorModeLevel MetaCognitive -ssRuleActivationTestMethod humanOracle -ssHintMethod humanDemonstration -ssIntroVideo metatutorMC.mp4 -ssCondition MetaTutorMC";;
  "-ct")
  condition="CogTutorControl"
  AddArgs="${AddArgs} -ssCogTutorMode  -ssHintMethod builtInClSolverTutor -ssRuleActivationTestMethod builtInClSolverTutor -ssIntroVideo cogTutor.mp4";;
	"-cta")
  condition="AplusControl"
  AddArgs="${AddArgs} -ssAplusCtrlCogTutorMode -ssProblemCheckerOracle ClOracle -ssHintMethod builtInClSolverTutor -ssRuleActivationTestMethod builtInClSolverTutor -ssIntroVideo aplus_controlS7.mp4";;
	"-mt")
  condition="MetaTutor"
  AddArgs="${AddArgs} -ssMetaTutorMode -ssProblemCheckerOracle ClOracle -ssRuleActivationTestMethod humanOracle -ssHintMethod humanDemonstration -ssIntroVideo metatutor.mp4";;
	"-mtc")
  condition="MetaTutorC"
  AddArgs="${AddArgs} -ssMetaTutorMode -ssProblemCheckerOracle ClOracle -ssMetaTutorModeLevel Cognitive -ssRuleActivationTestMethod humanOracle -ssHintMethod humanDemonstration -ssIntroVideo metatutorC.mp4";;
  "-bl")
  condition="AplusBaseline"
  AddArgs="${AddArgs} -ssRuleActivationTestMethod humanOracle -ssHintMethod humanDemonstration -ssIntroVideo metatutorC.mp4";;
  "-mtmc")
  condition="MetaTutorMC"
  AddArgs="${AddArgs} -ssMetaTutorMode -ssProblemCheckerOracle ClOracle -ssMetaTutorModeLevel MetaCognitive -ssRuleActivationTestMethod humanOracle -ssHintMethod humanDemonstration -ssIntroVideo metatutorMC.mp4";;
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
	echo "	-llmcti - turns constructive tutee inquiry with llm on"
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
"-cti") CTI="on"
condition="${condition}_CTI"
#AddArgs=${AddArgs/MetaTutorMC/MetaTutorMC_CTI}
kill -9 $(lsof -ti:8000)
sh ${CVSDIR}/runAplus_lightside.sh &
LIGHTSIDE_PID=$!
function cleanup {
  kill $LIGHTSIDE_PID
  kill $(lsof -t -i:8000)
}
trap cleanup EXIT
AddArgs="${AddArgs} -ssConstructiveTuteeInquiryFTIMode -ssCTIBothStuckParams none";;
"-llmcti") LLMCTI="on"
condition="${condition}_LLMCTI"

# CTI PYTHON SETUP
# Step 1: Find python3.12
PYTHON_PATH=$(command -v python3.12)
if [ -z "$PYTHON_PATH" ]; then
    echo "python3.12 not found in PATH."
    exit 1
fi
echo "Found python3.12 at: $PYTHON_PATH"
# Step 2: Create virtual environment in current directory (./venv)
$PYTHON_PATH -m venv .venv || { echo "Failed to create virtualenv"; exit 1; }
# Step 3: Retrieve python path from the virtual environment
VENV_PYTHON="${ProjectDir}/.venv/bin/python3.12" || { echo "Failed to locate python3.12 from the virtual environment"; exit 1; }
# Step 4: Install packages
$VENV_PYTHON -m pip install -r "${ProjectDir}/requirements.txt" || { echo "Failed to load packages from requirements.txt"; exit 1; }
echo "Virtual environment with python3.12 and other packages at: $VENV_PYTHON"

AddArgs="${AddArgs} -ssConstructiveTuteeInquiryResQLLM -DpythonScriptPath=${VENV_PYTHON}";;
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

AddArgs="${AddArgs} -ssCondition ${condition}"

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
TutorArg="-traceLevel 0"
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
#TutorArg="${TutorArg} -DconfigFile=simSt-config-cti.txt"
if [ ${CTI} == "on" ];
then
  TutorArg="${TutorArg} -ssResponseSatisfactoryGetterClass SimStAlgebraV8.AlgebraResponseSatisfactoryGetter"
  #TutorArg="${TutorArg} -DconfigFile=simSt-config-cti.txt"
fi
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
