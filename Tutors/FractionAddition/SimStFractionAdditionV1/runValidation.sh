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
	CVSDIR="${HOME}/CMU/pact-cvs-tree"
    fi
fi

#DorminJar="../lib/ctat.jar"
#DorminJar="${CVSDIR}/AuthoringTools_3_1/java/lib/ctat.jar"
DorminJar="${CVSDIR}/AuthoringToolsL/java/lib/ctat.jar"


if [ "${OS}" = "Windows_NT" ]; then
    CPATH="${DorminJar};..;."
fi
if [ "${OS}" != "Windows_NT" ]; then
    CPATH="${DorminJar}:..:."
fi

VmOption="-cp ${CPATH} -DnoCtatWindow -Xmx512m"


now=$(date)

# CTAT options
TutorArg="-traceLevel 3 -debugCodes nbarbaBrd"
TutorArg="${TutorArg} -ssPreservePrFile"
#TutorArg="${TutorArg} -ssInteractiveLearning"
#TutorArg="${TutorArg} -ssTestSet problems.txt"
TutorArg="${TutorArg} -ssTestSet Problems"
TutorArg="${TutorArg} -ssValidationMethod modeltracing"
#TutorArg="${TutorArg} -ssValidationMethod cogfi"
#TutorArg="${TutorArg} -ssModelTracingValidationOutcome Relaxed"
TutorArg="${TutorArg} -ssQuizGradingMethod JessOracle"
TutorArg="${TutorArg} -ssTestOutput $1"
TutorArg="${TutorArg} -ssCacheOracleInquiry false"
TutorArg="${TutorArg} -ssTestOnLastTrainingOnly"
TutorArg="${TutorArg} -ssSearchTimeOutDuration 60000"
TutorArg="${TutorArg} -ssMaxSearchDepth 3"
#TutorArg="${TutorArg} -ssClSolverTutorSAIConverter SimStAlgebraV8.AlgebraV8AdhocSAIConverter"
#TutorArg="${TutorArg} -ssSkillNameGetterClass SimStAlgebraV8.AlgebraV8AdhocSkillNameGetter"
#TutorArg="${TutorArg} -ssFoaGetterClass SimStAlgebraV8.AlgebraV8AdhocFoaGetter"
TutorArg="${TutorArg} -ssRuleActivationTestMethod builtInClSolverTutor"
TutorArg="${TutorArg} -ssPackageName SimStFractionAdditionV1"
TutorArg="${TutorArg} -ssProblemAccessorClass SimStFractionAdditionV1.FractionAdditionAssessor"
TutorArg="${TutorArg} -ssFoaClickDisabled"
TutorArg="${TutorArg} -ssHintMethod builtInClSolverTutor"
TutorArg="${TutorArg} -ssInputMatcher edu.cmu.pact.miss.userDef.algebra.IsEquivalent"
TutorArg="${TutorArg} -ssProjectDir ."
TutorArg="${TutorArg} -ssRunValidation"

#cmd="java ${VmOption} SimStAlgebraV7/SimStAlgebraV7 ${TutorArg}"
cmd="java ${VmOption} SimStFractionAdditionV1/fraction ${TutorArg}"
echo $cmd
$cmd 

for bload in *.bload
do
    rm -f ./"$bload"
done
