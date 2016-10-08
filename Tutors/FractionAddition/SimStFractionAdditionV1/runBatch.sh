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

VmOption="-cp ${CPATH} -Xmx512m"

# CTAT options

TutorArg="-traceLevel 5 -debugCodes miss ss"
TutorArg="${TutorArg} -ssBatchMode"
TutorArg="${TutorArg} -ssInteractiveLearning"
TutorArg="${TutorArg} -ssProblemSet problems.txt"
TutorArg="${TutorArg} -ssTestSet problemsTest.txt"
TutorArg="${TutorArg} -ssTestOutput outputfile.txt"
TutorArg="${TutorArg} -ssCacheOracleInquiry false"
TutorArg="${TutorArg} -ssSearchTimeOutDuration 60000"
TutorArg="${TutorArg} -ssMaxSearchDepth 3"
TutorArg="${TutorArg} -ssClSolverTutorSAIConverter SimStAlgebraV8.AlgebraV8AdhocSAIConverter"
TutorArg="${TutorArg} -ssSkillNameGetterClass SimStAlgebraV8.AlgebraV8AdhocSkillNameGetter"
TutorArg="${TutorArg} -ssFoaGetterClass SimStAlgebraV8.AlgebraV8AdhocFoaGetter"
TutorArg="${TutorArg} -ssInputCheckerClass SimStAlgebraV8.AlgebraV8InputChecker"
TutorArg="${TutorArg} -ssSelectionOrderGetterClass SimStAlgebraV8.AlgebraV8AdhocSelectionGetter"
TutorArg="${TutorArg} -ssRuleActivationTestMethod builtInClSolverTutor"
TutorArg="${TutorArg} -ssFoaClickDisabled"
TutorArg="${TutorArg} -ssHintMethod builtInClSolverTutor"
TutorArg="${TutorArg} -ssInputMatcher edu.cmu.pact.miss.userDef.algebra.IsEquivalent"
TutorArg="${TutorArg} -ssProjectDir ."

cmd="java ${VmOption} SimStAlgebraV8/SimStAlgebraV8 ${TutorArg}"
echo $cmd
$cmd

for bload in *.bload
do
    rm -f ./"$bload"
done
