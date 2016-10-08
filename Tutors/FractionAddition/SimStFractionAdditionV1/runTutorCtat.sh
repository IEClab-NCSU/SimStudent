#!/bin/bash

echo ${OS}

# Class path
# 
if [ "${CVSDIR}" = "" ]
then
    if [ "${OS}" = "Windows_NT" ]; then
	CVSDIR="f:/pact-cvs-tree"
    fi
    if [ "${OS}" != "Windows_NT" ]; then
	CVSDIR="${HOME}/mazda-on-Mac/Project/CTAT/CVS-TREE"
    fi
fi

CommJar="${CVSDIR}/AuthoringTools/java/lib/CommWidgets.jar"

if [ "${OS}" = "Windows_NT" ]; then
    CPATH="${CommJar};..;."
fi
if [ "${OS}" != "Windows_NT" ]; then
    CPATH="${CommJar}:..:."
fi



VmOption="-cp ${CPATH} -Xmx512m"

# CTAT options

TutorArg="-traceLevel 5 -debugCodes miss ss et"
TutorArg="${TutorArg} -ssRunInPLE"
TutorArg="${TutorArg} -ssSelfExplainMode"
#TutorArg="${TutorArg} -ssLogging"
TutorArg="${TutorArg} -ssLearnNoLabel"
#TutorArg="${TutorArg} -ssIntroVideo SimStudentIntroduction.avi"
TutorArg="${TutorArg} -ssLogURL http://pslc-qa.andrew.cmu.edu/log/server"
if [ $# -gt 0 ]; then
	TutorArg="${TutorArg} -ssUserID $1"
fi
TutorArg="${TutorArg} -ssCacheOracleInquiry false"
TutorArg="${TutorArg} -ssSearchTimeOutDuration 20000"
TutorArg="${TutorArg} -ssTutorServerTimeOutDuration 100000"
TutorArg="${TutorArg} -ssMaxSearchDepth 3"
#TutorArg="${TutorArg} -ssSkillNameGetterClass SimStAlgebraV7.AlgebraV7AdhocSkillNameGetter"
TutorArg="${TutorArg} -ssRuleActivationTestMethod humanOracle"
TutorArg="${TutorArg} -ssHintMethod humanDemonstration"
TutorArg="${TutorArg} -ssInputMatcher edu.cmu.pact.miss.userDef.algebra.IsEquivalent"
TutorArg="${TutorArg} -ssInputCheckerClass SimStAlgebraV7.AlgebraV7InputChecker"
TutorArg="${TutorArg} -ssFoaGetterClass SimStAlgebraV7.AlgebraV7AdhocFoaGetter"
TutorArg="${TutorArg} -ssClSolverTutorSAIConverter SimStAlgebraV7.AlgebraV7AdhocSAIConverter"
TutorArg="${TutorArg} -ssFoaClickDisabled"
TutorArg="${TutorArg} -ssProjectDir ."
TutorArg="${TutorArg} -ssNumBadInputRetries 2"
TutorArg="${TutorArg} -ssProblemsPerQuizSection 4"
TutorArg="${TutorArg} -ssCLQuizReqMode"
TutorArg="${TutorArg} -Dcourse_name=simStAlgebraSE"
TutorArg="${TutorArg} -Dschool_name=testSchool"
TutorArg="${TutorArg} -Dclass_name=testClass"

cmd="java ${VmOption} SimStAlgebraV7/SimStAlgebraV7 ${TutorArg}"
echo $cmd
$cmd

for bload in *.bload
do
    rm -f ./"$bload"
done
