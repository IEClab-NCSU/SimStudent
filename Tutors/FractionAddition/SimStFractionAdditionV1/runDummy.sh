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

VmOption="-cp ${CPATH} -DnoCtatWindow -Xmx512m"

# CTAT options

TutorArg="-traceLevel 5 -debugCodes ss"
TutorArg="${TutorArg} -ssContest"
TutorArg="${TutorArg} -ssDummyContest"
if [ $# -gt 0 ]; then
	TutorArg="${TutorArg} -ssUserID $1"
fi
TutorArg="${TutorArg} -ssLoadPrefsFile brPrefsCtat.xml"
TutorArg="${TutorArg} -ssCacheOracleInquiry false"
TutorArg="${TutorArg} -ssSearchTimeOutDuration 20000"
TutorArg="${TutorArg} -ssTutorServerTimeOutDuration 100000"
TutorArg="${TutorArg} -ssMaxSearchDepth 3"
TutorArg="${TutorArg} -ssSkillNameGetterClass SimStAlgebraV7.AlgebraV7AdhocSkillNameGetter"
TutorArg="${TutorArg} -ssRuleActivationTestMethod humanOracle"
TutorArg="${TutorArg} -ssHintMethod humanDemonstration"
TutorArg="${TutorArg} -ssInputMatcher edu.cmu.pact.miss.userDef.algebra.IsEquivalent"
TutorArg="${TutorArg} -ssFoaGetterClass SimStAlgebraV7.AlgebraV7AdhocFoaGetter"
TutorArg="${TutorArg} -ssInputCheckerClass SimStAlgebraV7.AlgebraV7InputChecker"
TutorArg="${TutorArg} -ssClSolverTutorSAIConverter SimStAlgebraV7.AlgebraV7AdhocSAIConverter"
TutorArg="${TutorArg} -ssProjectDir ."
TutorArg="${TutorArg} -ssNumBadInputRetries 2"
TutorArg="${TutorArg} -ssFoaClickDisabled"
TutorArg="${TutorArg} -ssProblemsPerQuizSection 3"


cmd="java ${VmOption} SimStAlgebraV7/SimStAlgebraV7 ${TutorArg}"
echo $cmd
$cmd

for bload in *.bload
do
    rm -f ./"$bload"
done
