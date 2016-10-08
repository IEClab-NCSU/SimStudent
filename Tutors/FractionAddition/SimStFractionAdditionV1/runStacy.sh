#!/bin/bash

echo ${OS}

# Class path
# 
if [ "${CVSDIR}" = "" ]
then
    if [ "${OS}" = "Windows_NT" ]; then
	CVSDIR="f:/pact-cvs-tree"
	CPS=";"
    fi
    if [ "${OS}" != "Windows_NT" ]; then
	CVSDIR="${HOME}/mazda-on-Mac/Project/CTAT/CVS-TREE"
	CPS=":"
    fi
fi

CtatJar="${CVSDIR}/AuthoringTools/java/lib/ctat.jar${CPS}${CVSDIR}/AuthoringTools/java/lib/jess.jar"

if [ "${OS}" = "Windows_NT" ]; then
    CPATH="${CtatJar};..;."
fi
if [ "${OS}" != "Windows_NT" ]; then
    CPATH="${CtatJar}:..:."
fi

VmOption="-cp ${CPATH} -DnoCtatWindow -Xmx512m"

# CTAT options

TutorArg="-traceLevel 5 -debugCodes miss ss"
TutorArg="${TutorArg} -ssRunInPLE"
#TutorArg="${TutorArg} -ssSelfExplainMode"
#TutorArg="${TutorArg} -ssLogging"
TutorArg="${TutorArg} -ssLocalLogging"
TutorArg="${TutorArg} -ssIntroVideo demoVideoBriefNoSE.mov"
TutorArg="${TutorArg} -ssOverviewPage curriculum.html"
TutorArg="${TutorArg} -ssLoadPrefsFile brPrefsStacy.xml"
TutorArg="${TutorArg} -ssLogURL http://mocha.pslc.cs.cmu.edu/Servlet/log"
if [ $# -gt 0 ]; then
	TutorArg="${TutorArg} -ssUserID $1"
fi
TutorArg="${TutorArg} -ssCacheOracleInquiry false"
TutorArg="${TutorArg} -ssSearchTimeOutDuration 20000"
TutorArg="${TutorArg} -ssTutorServerTimeOutDuration 100000"
TutorArg="${TutorArg} -ssMaxSearchDepth 3"
TutorArg="${TutorArg} -ssSkillNameGetterClass SimStAlgebraV8.AlgebraV8AdhocSkillNameGetter"
TutorArg="${TutorArg} -ssRuleActivationTestMethod humanOracle"
TutorArg="${TutorArg} -ssHintMethod humanDemonstration"
TutorArg="${TutorArg} -ssInputMatcher edu.cmu.pact.miss.userDef.algebra.IsEquivalent"
TutorArg="${TutorArg} -ssInputCheckerClass SimStAlgebraV8.AlgebraV8InputChecker"
TutorArg="${TutorArg} -ssFoaGetterClass SimStAlgebraV8.AlgebraV8AdhocFoaGetter"
TutorArg="${TutorArg} -ssSelectionOrderGetterClass SimStAlgebraV8.AlgebraV8AdhocSelectionGetter"
TutorArg="${TutorArg} -ssClSolverTutorSAIConverter SimStAlgebraV8.AlgebraV8AdhocSAIConverter"
TutorArg="${TutorArg} -ssActivationList AccuracySortedActivationList"
TutorArg="${TutorArg} -ssFoaClickDisabled"
TutorArg="${TutorArg} -ssProjectDir ."
TutorArg="${TutorArg} -ssNumBadInputRetries 2"
TutorArg="${TutorArg} -ssProblemsPerQuizSection 2"
TutorArg="${TutorArg} -ssCLQuizReqMode"
TutorArg="${TutorArg} -Dcourse_name=simStAlgebraStacy"
TutorArg="${TutorArg} -Dschool_name=testSchool"
TutorArg="${TutorArg} -Dclass_name=testClass"
TutorArg="${TutorArg} -ssCondition devTesting"

cmd="java ${VmOption} SimStAlgebraV8/SimStAlgebraV8 ${TutorArg}"
echo $cmd
$cmd

for bload in *.bload
do
    rm -f ./"$bload"
done
