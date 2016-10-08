#!/bin/bash

# Class path
# 


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

CtatJar="${CVSDIR}/AuthoringToolsL/java/lib/ctat.jar${CPS}${CVSDIR}/AuthoringToolsL/java/lib/jess.jar"


if [ "${OS}" = "Windows_NT" ]; then
    CPATH="${CtatJar};..;."
fi
if [ "${OS}" != "Windows_NT" ]; then
    CPATH="${CtatJar}:..:."
fi


VmOption="-cp ${CPATH} -Xmx512m -DssFoilBase=../FOIL6 -DnoCtatWindow"

# CTAT options

TutorArg="-traceLevel 5 -debugCodes miss ss"
TutorArg="${TutorArg} -ssRunInPLE"
TutorArg="${TutorArg} -ssCacheOracleInquiry false"
TutorArg="${TutorArg} -ssSearchTimeOutDuration 20000"
TutorArg="${TutorArg} -ssMaxSearchDepth 3"
TutorArg="${TutorArg} -ssRuleActivationTestMethod humanOracle"
TutorArg="${TutorArg} -ssQuizGradingMethod JessOracle"
TutorArg="${TutorArg} -ssSelectionOrderGetterClass SimStFractionAdditionV1.FractionAdditionAdhocSelectionGetter"
TutorArg="${TutorArg} -ssSkillNameGetterClass SimStFractionAdditionV1.FractionAdditionAdhocSkillNameGetter"
TutorArg="${TutorArg} -ssFoaGetterClass SimStFractionAdditionV1.FractionAdditionAdhocFoaGetter"
TutorArg="${TutorArg} -ssPackageName SimStFractionAdditionV1"
TutorArg="${TutorArg} -ssProblemAccessorClass SimStFractionAdditionV1.FractionAdditionAssessor"
TutorArg="${TutorArg} -ssHintMethod humanDemonstration"
TutorArg="${TutorArg} -ssInputMatcher edu.cmu.pact.miss.userDef.algebra.IsEquivalent"
TutorArg="${TutorArg} -ssFoaClickDisabled"
TutorArg="${TutorArg} -ssProjectDir ."
TutorArg="${TutorArg} -Dcourse_name=simStTutor -Dschool=pilot"

cmd="java ${VmOption} SimStFractionAdditionV1/fraction ${TutorArg}"
echo $cmd
$cmd

for bload in *.bload
do
    rm -f ./"$bload"
done
