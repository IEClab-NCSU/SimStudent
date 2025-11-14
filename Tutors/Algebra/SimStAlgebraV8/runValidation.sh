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
	CVSDIR="${HOME}/Desktop/SimStudentGithub/SimStudent-master"
    fi
fi

#DorminJar="../lib/ctat.jar"
DorminJar="${CVSDIR}/APLUS/java/lib/ctat.jar"


if [ "${OS}" = "Windows_NT" ]; then
    CPATH="${DorminJar};..;."
fi
if [ "${OS}" != "Windows_NT" ]; then
    CPATH="${DorminJar}:..:."
fi

VmOption="-cp ${CPATH} -DnoCtatWindow -Xmx512m"


now=$(date)

# CTAT options
TutorArg="-traceLevel 5 -debugCodes miss ss rr"
TutorArg="${TutorArg} -ssPreservePrFile"
#TutorArg="${TutorArg} -ssInteractiveLearning"
TutorArg="${TutorArg} -ssTestSet problems.txt"
#TutorArg="${TutorArg} -ssTestSet Problems"
TutorArg="${TutorArg} -ssTestOutput $1"
TutorArg="${TutorArg} -ssCacheOracleInquiry false"
TutorArg="${TutorArg} -ssTestOnLastTrainingOnly"
TutorArg="${TutorArg} -ssSearchTimeOutDuration 60000"
TutorArg="${TutorArg} -ssMaxSearchDepth 3"
TutorArg="${TutorArg} -ssClSolverTutorSAIConverter SimStAlgebraV8.AlgebraV8AdhocSAIConverter"
TutorArg="${TutorArg} -ssSkillNameGetterClass SimStAlgebraV8.AlgebraV8AdhocSkillNameGetter"
TutorArg="${TutorArg} -ssFoaGetterClass SimStAlgebraV8.AlgebraV8AdhocFoaGetter"
TutorArg="${TutorArg} -ssStepNameGetterClass SimStAlgebraV8.AlgebraV8AdhocStepNameGetter"
TutorArg="${TutorArg} -ssRuleActivationTestMethod builtInClSolverTutor"
TutorArg="${TutorArg} -ssFoaClickDisabled"
TutorArg="${TutorArg} -ssHintMethod builtInClSolverTutor"
TutorArg="${TutorArg} -ssInputMatcher edu.cmu.pact.miss.userDef.algebra.IsEquivalent"
TutorArg="${TutorArg} -ssProjectDir ."
TutorArg="${TutorArg} -ssRunValidation"

#cmd="java ${VmOption} SimStAlgebraV7/SimStAlgebraV7 ${TutorArg}"
cmd="java ${VmOption} SimStAlgebraV8/SimStAlgebraV8 ${TutorArg}"
echo $cmd
$cmd >outputs.txt

for bload in *.bload
do
    rm -f ./"$bload"
done
