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

#CtatJar="../lib1/ctat.jar${CPS}../lib1/jess.jar"
CtatJar="${CVSDIR}/AuthoringTools_3_1/java/lib/ctat.jar${CPS}${CVSDIR}/AuthoringTools_3_1/java/lib/jess.jar"

if [ "${OS}" = "Windows_NT" ]; then
    CPATH="${CtatJar};..;."
fi
if [ "${OS}" != "Windows_NT" ]; then
    CPATH="${CtatJar}:..:."
fi

redir="off"

for i do
case $i in
"-nil") IL="off";;
"-br") br="on";;
"-pr") AddArgs="${AddArgs} -ssPreservePrFile";;
"-last"|"-testLast") AddArgs="${AddArgs} -ssTestOnLastTrainingOnly";;
"-train") AddArgs="${AddArgs} -ssProblemSet";  train="on";;
"-test") AddArgs="${AddArgs} -ssTestSet"; tests="on";;
"-results"|"-testOutput") AddArgs="${AddArgs} -ssTestOutput";;
"-maxTrain") AddArgs="${AddArgs} -ssSetMaxNumTraining";;
"-maxTest") AddArgs="${AddArgs} -ssSetMaxNumTest";;
"-o"|"-output") redir="on";;
"-h"|"-help") echo "Usage: $0"
echo "-nil - use non-interactive learning instead of interactive learning"
echo "-br - displays Behavior Recorder window"
echo "-pr - preserve existing production rule file"
echo "-last or -testLast - tests only after the last training when set"
echo "-train <filename> - specifies a file of problems to train on"
echo "-test <filename> - specifies a file of problems to validate on"
echo "-testOutput or -results <filename> - specifies a file to output test results to"
echo "-maxTrain <#> - sets the maximum number of training examples to use"
echo "-maxTest <#> - sets the maximum number of testing examples to use"
echo "-o or -output <filename> - redirects output to file"
echo "Must specify at least one of train or test"
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

if [ -z ${train} ];
then
	if [ -z ${tests} ];
	then
		echo "Usage: $0"
		echo "-nil - use non-interactive learning instead of interactive learning"
		echo "-br - displays Behavior Recorder window"
		echo "-pr - preserve existing production rule file"
		echo "-last or -testLast - tests only after the last training when set"
		echo "-train <filename> - specifies a file of problems to train on"
		echo "-test <filename> - specifies a file of problems to validate on"
		echo "-testOutput or -results <filename> - specifies a file to output test results to"
		echo "-maxTrain <#> - sets the maximum number of training examples to use"
		echo "-maxTest <#> - sets the maximum number of testing examples to use"
		echo "-o or -output <filename> - redirects output to file"
		echo "*Must specify at least one of train or test*"
		exit;
	fi
fi

# VmOption="-cp ${CPATH} -DnoCtatWindow -Xmx512m"
if [ -z $br ];
then
VmOption="-cp ${CPATH} -DnoCtatWindow -Xmx512m";
else
VmOption="-cp ${CPATH} -Xmx512m"
fi
# CTAT options

TutorArg="-traceLevel 3 -debugCodes miss ss"
if [ -z ${IL} ];
then
	TutorArg="${TutorArg} -ssInteractiveLearning";
else
	TutorArg="${TutorArg} -ssNonInteractiveLearning";
fi
if [ -z ${train} ];
then
	TutorArg="${TutorArg} -ssRunValidation";
else
	TutorArg="${TutorArg} -ssBatchMode";
fi
TutorArg="${TutorArg} -ssDontShowAllRaWhenTutored"
TutorArg="${TutorArg} -ssFixedLearningMode"
TutorArg="${TutorArg} -ssCacheOracleInquiry false"
TutorArg="${TutorArg} -ssSearchTimeOutDuration 60000"
TutorArg="${TutorArg} -ssTutorServerTimeOutDuration 100000"
TutorArg="${TutorArg} -ssMaxSearchDepth 3"
TutorArg="${TutorArg} -ssSkillNameGetterClass SimStAlgebraV8.AlgebraV8AdhocSkillNameGetter"
TutorArg="${TutorArg} -ssInterfaceElementGetterClass SimStAlgebraV8.AlgebraV8AdhocInterfaceElementGetter"
TutorArg="${TutorArg} -ssRuleActivationTestMethod builtInClSolverTutor"
TutorArg="${TutorArg} -ssHintMethod builtInClSolverTutor"
TutorArg="${TutorArg} -ssInputMatcher edu.cmu.pact.miss.userDef.algebra.IsEquivalent"
TutorArg="${TutorArg} -ssInputCheckerClass SimStAlgebraV8.AlgebraV8InputChecker"
TutorArg="${TutorArg} -ssFoaGetterClass SimStAlgebraV8.AlgebraV8AdhocFoaGetter"
TutorArg="${TutorArg} -ssStepNameGetterClass SimStAlgebraV8.AlgebraV8AdhocStepNameGetter"
TutorArg="${TutorArg} -ssSelectionOrderGetterClass SimStAlgebraV8.AlgebraV8AdhocSelectionGetter"
TutorArg="${TutorArg} -ssClSolverTutorSAIConverter SimStAlgebraV8.AlgebraV8AdhocSAIConverter"
TutorArg="${TutorArg} -ssActivationList AccuracySortedActivationList"
TutorArg="${TutorArg} -ssFoaClickDisabled"
TutorArg="${TutorArg} -ssProjectDir ."
TutorArg="${TutorArg} -ssNumBadInputRetries 2"
TutorArg="${TutorArg} -ssProblemsPerQuizSection 2"
TutorArg="${TutorArg} -Dcourse_name=simStAlgebraTesting"
TutorArg="${TutorArg} -Dschool_name=testSchool"
TutorArg="${TutorArg} -Dclass_name=testClass"
TutorArg="${TutorArg} -ssCondition devTesting"
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

for bload in *.bload
do
    rm -f ./"$bload"
done
