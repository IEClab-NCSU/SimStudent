#!/bin/bash

if [ "${CVSDIR}" = "" ]
then
    if [ "${OS}" = "Windows_NT" ]; then
	CVSDIR="c:/pact-cvs-tree"
	CPS=";"
    fi
    if [ "${OS}" != "Windows_NT" ]; then
	CVSDIR="${HOME}/CMU/pact-cvs-tree"
	CPS=":"
    fi
fi

CtatJar="../lib/ctat.jar${CPS}../lib/jess.jar"
#CtatJar="${CVSDIR}/AuthoringToolsL/java/lib/ctat.jar${CPS}${CVSDIR}/AuthoringToolsL/java/lib/jess.jar"


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


# VmOption="-cp ${CPATH} -DnoCtatWindow -Xmx512m"
if [ -z $br ];
then
VmOption="-cp ${CPATH} -DnoCtatWindow -Xmx512m -DssFoilBase=../FOIL6";
else
VmOption="-cp ${CPATH} -Xmx512m -DssFoilBase=../../FOIL6"
fi
# CTAT options

TutorArg="-traceLevel 3 -debugCodes nbarba miss ss wme sswme sslist"
#TutorArg="-traceLevel 3 -debugCodes ssDebug"
if [ -z ${PLE} ];
then
	TutorArg="${TutorArg} -ssRunInPLE";
fi
if [ -z ${SE} ];
then
	TutorArg="${TutorArg} -ssSelfExplainMode";
fi
#TutorArg="${TutorArg} -ssIntroVideo demoVideoBriefNoSE.mov"
TutorArg="${TutorArg} -ssOverviewPage curriculum.html"
TutorArg="${TutorArg} -ssLoadPrefsFile brPrefsStacy.xml"
TutorArg="${TutorArg} -ssCacheOracleInquiry false"
TutorArg="${TutorArg} -ssSearchTimeOutDuration 20000"
TutorArg="${TutorArg} -ssTutorServerTimeOutDuration 100000"
TutorArg="${TutorArg} -ssMaxSearchDepth 3"
TutorArg="${TutorArg} -ssLocalLogging true"
TutorArg="${TutorArg} -ssLogging true"
#TutorArg="${TutorArg} -ssLogURL http://pslc-qa.andrew.cmu.edu/log/server"
TutorArg="${TutorArg} -ssLogURL http://learnlab.web.cmu.edu/log/server"
TutorArg="${TutorArg} -ssRuleActivationTestMethod humanOracle"
TutorArg="${TutorArg} -ssQuizGradingMethod JessOracle"
TutorArg="${TutorArg} -ssHintMethod humanDemonstration"
TutorArg="${TutorArg} -ssInputMatcher edu.cmu.pact.miss.userDef.algebra.IsEquivalent"
TutorArg="${TutorArg} -ssActivationList AccuracySortedActivationList"
TutorArg="${TutorArg} -ssSelectionOrderGetterClass SimStFractionAdditionV1.FractionAdditionAdhocSelectionGetter"
TutorArg="${TutorArg} -ssSkillNameGetterClass SimStFractionAdditionV1.FractionAdditionAdhocSkillNameGetter"
TutorArg="${TutorArg} -ssFoaGetterClass SimStFractionAdditionV1.FractionAdditionAdhocFoaGetter"
#TutorArg="${TutorArg} -ssInputCheckerClass SimStFractionAdditionV1.FractionAdditionInputChecker"
TutorArg="${TutorArg} -ssFoaClickDisabled"
TutorArg="${TutorArg} -ssProjectDir ."
TutorArg="${TutorArg} -ssPackageName SimStFractionAdditionV1"
TutorArg="${TutorArg} -ssProblemAccessorClass SimStFractionAdditionV1.FractionAdditionAssessor"
TutorArg="${TutorArg} -ssNumBadInputRetries 2"
TutorArg="${TutorArg} -ssFoilMaxTuples 15000000"
TutorArg="${TutorArg} -ssProblemsPerQuizSection 2"
TutorArg="${TutorArg} -Dcourse_name=SimStFractionPilotSep"
TutorArg="${TutorArg} -Dschool_name=cmu"
TutorArg="${TutorArg} -Dclass_name=cmu"
TutorArg="${TutorArg} -ssCondition pilotSession"
TutorArg="${TutorArg} ${AddArgs}"

cmd="java ${VmOption} SimStFractionAdditionV1/fraction ${TutorArg}"
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
