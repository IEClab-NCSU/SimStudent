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

CommJar="${CVSDIR}/AuthoringTools/java/lib/ctat.jar"

if [ "${OS}" = "Windows_NT" ]; then
    CPATH="${CommJar};..;."
fi
if [ "${OS}" != "Windows_NT" ]; then
    CPATH="${CommJar}:..:."
fi

VmOption="-cp ${CPATH} -DnoCtatWindow -Xmx512m"

# CTAT options

TutorArg="-traceLevel 5 -debugCodes ss"
TutorArg="${TutorArg} -ssContest"
#if [ $# -gt 0 ]; then
#	TutorArg="${TutorArg} -ssSimStName $1"
#fi
#if [ $# -gt 1 ]; then
#	TutorArg="${TutorArg} -ssSimStImage $2"
#fi
if [ $# -gt 0 ]; then
	TutorArg="${TutorArg} -ssUserID $1"
fi
TutorArg="${TutorArg} -ssLogging"
TutorArg="${TutorArg} -ssLocalLogging"
TutorArg="${TutorArg} -ssLogURL http://mocha.pslc.cs.cmu.edu/Servlet/log"
TutorArg="${TutorArg} -ssLoadPrefsFile brPrefsCtat.xml"
TutorArg="${TutorArg} -ssCacheOracleInquiry false"
TutorArg="${TutorArg} -ssSearchTimeOutDuration 20000"
TutorArg="${TutorArg} -ssTutorServerTimeOutDuration 100000"
TutorArg="${TutorArg} -ssMaxSearchDepth 3"
TutorArg="${TutorArg} -ssSkillNameGetterClass SimStAlgebraV8.AlgebraV8AdhocSkillNameGetter"
TutorArg="${TutorArg} -ssRuleActivationTestMethod humanOracle"
TutorArg="${TutorArg} -ssHintMethod humanDemonstration"
TutorArg="${TutorArg} -ssInputMatcher edu.cmu.pact.miss.userDef.algebra.IsEquivalent"
TutorArg="${TutorArg} -ssFoaGetterClass SimStAlgebraV8.AlgebraV8AdhocFoaGetter"
TutorArg="${TutorArg} -ssInputCheckerClass SimStAlgebraV8.AlgebraV8InputChecker"
TutorArg="${TutorArg} -ssClSolverTutorSAIConverter SimStAlgebraV8.AlgebraV8AdhocSAIConverter"
TutorArg="${TutorArg} -ssProjectDir ."
TutorArg="${TutorArg} -ssFoaClickDisabled"
TutorArg="${TutorArg} -ssNumBadInputRetries 2"
TutorArg="${TutorArg} -ssProblemsPerQuizSection 3"
TutorArg="${TutorArg} -Dcourse_name=simStGameShowTest"
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
