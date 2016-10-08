#!/bin/bash

echo ${OS}

# 
## Change these variables 
#

if [ "${CVSDIR}" = "" ]
then
	if [ "${OS}" = "Windows_NT" ]
	then
		CVSDIR="f:/pact-cvs-tree"
	fi
	if [ "${OS}" != "Windows_NT" ]
	then
		CVSDIR="${HOME}/CMU/pact-cvs-tree"
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

#CommJar="../lib1/ctat.jar${CPS}../lib1/jess.jar"
CommJar="${CVSDIR}/AuthoringToolsL/java/lib/ctat.jar${CPS}${CVSDIR}/AuthoringToolsL/java/lib/jess.jar"
CPATH="${CommJar}${CPS}..${CPS}."

# CPATH="f:/Project/CTAT/CVS-TREE/AuthoringTools/java/lib/CommWidgets.jar;..;."

VmOption="-classpath ${CPATH}"

echo compiling Tutor interface...
echo ${JAVAC} ${VmOption} fraction.java -source 1.6 -target 1.6
${JAVAC} ${VmOption} fraction.java -source 1.6 -target 1.6

echo compiling UserDefSymbols...
echo ${JAVAC} ${VmOption} UserDefSymbols.java -source 1.6 -target 1.6
${JAVAC} ${VmOption} UserDefSymbols.java -source 1.6 -target 1.6

echo compiling FractionAdditionAdhocFoaGetter...
echo ${JAVAC} ${VmOption} FractionAdditionAdhocFoaGetter.java -source 1.6 -target 1.6
${JAVAC} ${VmOption} FractionAdditionAdhocFoaGetter.java -source 1.6 -target 1.6

echo compiling FractionAdditionAdhocSelectionGetter...
echo ${JAVAC} ${VmOption} FractionAdditionAdhocSelectionGetter.java -source 1.6 -target 1.6
${JAVAC} ${VmOption} FractionAdditionAdhocSelectionGetter.java -source 1.6 -target 1.6


echo compiling FractionAdditionAdhocSkillNameGetter...
echo ${JAVAC} ${VmOption} FractionAdditionAdhocSkillNameGetter.java -source 1.6 -target 1.6
${JAVAC} ${VmOption} FractionAdditionAdhocSkillNameGetter.java -source 1.6 -target 1.6

echo compiling FractionAdditionInputChecker...
echo ${JAVAC} ${VmOption} FractionAdditionInputChecker.java -source 1.6 -target 1.6
${JAVAC} ${VmOption} FractionAdditionInputChecker.java -source 1.6 -target 1.6

echo compiling FractionAdditionAdhocFoaGetter...
echo ${JAVAC} ${VmOption} FractionAdditionAdhocFoaGetter.java -source 1.6 -target 1.6
${JAVAC} ${VmOption} FractionAdditionAdhocFoaGetter.java -source 1.6 -target 1.6


echo compiling FractionAdditionAssessor...
echo ${JAVAC} ${VmOption} FractionAdditionAssessor.java -source 1.6 -target 1.6
${JAVAC} ${VmOption} FractionAdditionAssessor.java -source 1.6 -target 1.6

echo compiling GetMixedFractionInteger...
echo ${JAVAC} ${VmOption} ./operators/GetMixedFractionInteger.java  -source 1.6 -target 1.6
"${JAVAC}" ${VmOption} ./operators/GetMixedFractionInteger.java  -source 1.6 -target 1.6

echo compiling DivisionQuotient...
echo ${JAVAC} ${VmOption} ./operators/DivisionQuotient.java
"${JAVAC}" ${VmOption} ./operators/DivisionQuotient.java

echo compiling DivisionRemainder...
echo ${JAVAC} ${VmOption} ./operators/DivisionRemainder.java
"${JAVAC}" ${VmOption} ./operators/DivisionRemainder.java

echo compiling IsEquivalentFraction...
echo ${JAVAC} ${VmOption} ./featurePredicates/IsEquivalentFraction.java
"${JAVAC}" ${VmOption} ./featurePredicates/IsEquivalentFraction.java

echo compiling GetMixedFractionNumerator...
echo ${JAVAC} ${VmOption} ./operators/GetMixedFractionNumerator.java  -source 1.6 -target 1.6
"${JAVAC}" ${VmOption} ./operators/GetMixedFractionNumerator.java  -source 1.6 -target 1.6

echo compiling IsImproperFraction...
echo ${JAVAC} ${VmOption} ./featurePredicates/IsImproperFraction.java  -source 1.6 -target 1.6
"${JAVAC}" ${VmOption} ./featurePredicates/IsImproperFraction.java  -source 1.6 -target 1.6

echo compiling IsEmpty...
echo ${JAVAC} ${VmOption} ./featurePredicates/IsEmpty.java  -source 1.6 -target 1.6
"${JAVAC}" ${VmOption} ./featurePredicates/IsEmpty.java  -source 1.6 -target 1.6