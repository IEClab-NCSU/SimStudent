#!/bin/bash

echo ${OS}
echo ${JAVA_HOME}

# 
## Change these variables 
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

JAVAC="javac"

#
## Don't change following codes
#
if [ "${OS}" = "Windows_NT" ]
then
	CPS=";"
fi
if [ "${OS}" != "Windows_NT" ]
then
	CPS=":"
fi

DorminJar="${CVSDIR}/AuthoringToolsL/java/lib/ctat.jar:${CVSDIR}/AuthoringToolsL/java/lib/jess.jar"

CPATH="${DorminJar}${CPS}..${CPS}."

# CPATH="f:/Project/CTAT/CVS-TREE/AuthoringTools/java/lib/DorminWidgets.jar;..;."

VmOption="-classpath ${CPATH}"

#echo compiling Tutor interface
#echo ${JAVAC} ${VmOption} ThreeStepEq.java
#${JAVAC} ${VmOption} ThreeStepEq.java

echo compiling UserDefSymbols...
echo ${JAVAC} ${VmOption} UserDefSymbols.java -source 1.6 -target 1.6
"${JAVAC}" ${VmOption} UserDefSymbols.java -source 1.6 -target 1.6

echo compiling fraction...
echo ${JAVAC} ${VmOption} fraction.java -source 1.6 -target 1.6
"${JAVAC}" ${VmOption} fraction.java -source 1.6 -target 1.6


echo compiling DivisionQuotient...
echo ${JAVAC} ${VmOption} ./operators/DivisionQuotient.java -source 1.6 -target 1.6
"${JAVAC}" ${VmOption} ./operators/DivisionQuotient.java -source 1.6 -target 1.6

echo compiling DivisionRemainder...
echo ${JAVAC} ${VmOption} ./operators/DivisionRemainder.java -source 1.6 -target 1.6
"${JAVAC}" ${VmOption} ./operators/DivisionRemainder.java -source 1.6 -target 1.6

echo compiling IsImproperFraction...
echo ${JAVAC} ${VmOption} ./featurePredicates/IsImproperFraction.java -source 1.6 -target 1.6
"${JAVAC}" ${VmOption} ./featurePredicates/IsImproperFraction.java -source 1.6 -target 1.6

echo compiling IsEmpty...
echo ${JAVAC} ${VmOption} ./featurePredicates/IsEmpty.java -source 1.6 -target 1.6
"${JAVAC}" ${VmOption} ./featurePredicates/IsEmpty.java -source 1.6 -target 1.6

echo compiling IsEquivalentFraction...
echo ${JAVAC} ${VmOption} ./featurePredicates/IsEquivalentFraction.java -source 1.6 -target 1.6
"${JAVAC}" ${VmOption} ./featurePredicates/IsEquivalentFraction.java -source 1.6 -target 1.6

echo compiling SelectReduce...
echo ${JAVAC} ${VmOption} ./operators/SelectReduce.java -source 1.6 -target 1.6
"${JAVAC}" ${VmOption} ./operators/SelectReduce.java -source 1.6 -target 1.6

echo compiling SelectAdd...
echo ${JAVAC} ${VmOption} ./operators/SelectAdd.java -source 1.6 -target 1.6
"${JAVAC}" ${VmOption} ./operators/SelectAdd.java -source 1.6 -target 1.6

echo compiling SelectSimplify...
echo ${JAVAC} ${VmOption} ./operators/SelectSimplify.java -source 1.6 -target 1.6
"${JAVAC}" ${VmOption} ./operators/SelectSimplify.java -source 1.6 -target 1.6

echo compiling SelectComplex...
echo ${JAVAC} ${VmOption} ./operators/SelectComplex.java -source 1.6 -target 1.6
"${JAVAC}" ${VmOption} ./operators/SelectComplex.java -source 1.6 -target 1.6

echo compiling IsGoalReduce...
echo ${JAVAC} ${VmOption} ./featurePredicates/IsGoalReduce.java -source 1.6 -target 1.6
"${JAVAC}" ${VmOption} ./featurePredicates/IsGoalReduce.java -source 1.6 -target 1.6

echo compiling IsGoalAdd...
echo ${JAVAC} ${VmOption} ./featurePredicates/IsGoalAdd.java -source 1.6 -target 1.6
"${JAVAC}" ${VmOption} ./featurePredicates/IsGoalAdd.java -source 1.6 -target 1.6

echo compiling IsGoalSimplify...
echo ${JAVAC} ${VmOption} ./featurePredicates/IsGoalSimplify.java -source 1.6 -target 1.6
"${JAVAC}" ${VmOption} ./featurePredicates/IsGoalSimplify.java -source 1.6 -target 1.6

echo compiling IsGoalComplex...
echo ${JAVAC} ${VmOption} ./featurePredicates/IsGoalComplex.java -source 1.6 -target 1.6
"${JAVAC}" ${VmOption} ./featurePredicates/IsGoalComplex.java -source 1.6 -target 1.6

echo compiling FractionAdditionAdhocFoaGetter...
echo ${JAVAC} ${VmOption} ./FractionAdditionAdhocFoaGetter.java -source 1.6 -target 1.6
"${JAVAC}" ${VmOption} ./FractionAdditionAdhocFoaGetter.java -source 1.6 -target 1.6

echo compiling FractionAdditionAdhocSkillNameGetter...
echo ${JAVAC} ${VmOption} ./FractionAdditionAdhocSkillNameGetter.java -source 1.6 -target 1.6
"${JAVAC}" ${VmOption} ./FractionAdditionAdhocSkillNameGetter.java -source 1.6 -target 1.6

echo compiling IsComplexFractionChunkImproper...
echo ${JAVAC} ${VmOption} ./featurePredicates/IsComplexFractionChunkImproper.java -source 1.6 -target 1.6
"${JAVAC}" ${VmOption} ./featurePredicates/IsComplexFractionChunkImproper.java -source 1.6 -target 1.6

echo compiling CanComplexFractionBeSimplified...
echo ${JAVAC} ${VmOption} ./featurePredicates/CanComplexFractionBeSimplified.java -source 1.6 -target 1.6
"${JAVAC}" ${VmOption} ./featurePredicates/CanComplexFractionBeSimplified.java -source 1.6 -target 1.6

echo compiling IsComplexFractionChunkProper...
echo ${JAVAC} ${VmOption} ./featurePredicates/IsComplexFractionChunkProper.java -source 1.6 -target 1.6
"${JAVAC}" ${VmOption} ./featurePredicates/IsComplexFractionChunkProper.java -source 1.6 -target 1.6

echo compiling IsComplexFractionChunkComplex...
echo ${JAVAC} ${VmOption} ./featurePredicates/IsComplexFractionChunkComplex.java -source 1.6 -target 1.6
"${JAVAC}" ${VmOption} ./featurePredicates/IsComplexFractionChunkComplex.java -source 1.6 -target 1.6

echo compiling CanBeSimplified...
echo ${JAVAC} ${VmOption} ./featurePredicates/CanBeSimplified.java -source 1.6 -target 1.6
"${JAVAC}" ${VmOption} ./featurePredicates/CanBeSimplified.java -source 1.6 -target 1.6


#echo compiling AlgebraOneAdhocFoaGetter...
#echo ${JAVAC} ${VmOption} AlgebraOneAdhocFoaGetter.java
#${JAVAC} ${VmOption} AlgebraOneAdhocFoaGetter.java

