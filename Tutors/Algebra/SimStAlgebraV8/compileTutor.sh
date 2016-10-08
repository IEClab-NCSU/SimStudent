#!/bin/bash

echo ${OS}
cd ..
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
		CVSDIR="${HOME}/Desktop/SimStudentGithub/SimStudent-master"
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
CommJar="${CVSDIR}/APLUS/java/lib/ctat.jar${CPS}${CVSDIR}/APLUS/java/lib/jess.jar"
CPATH="${CommJar}${CPS}..${CPS}."

# CPATH="f:/Project/CTAT/CVS-TREE/AuthoringTools/java/lib/CommWidgets.jar;..;."

VmOption="-classpath ${CPATH}"

echo compiling Tutor interface...
echo ${JAVAC} ${VmOption} SimStAlgebraV8.java
${JAVAC} ${VmOption} SimStAlgebraV8/SimStAlgebraV8.java -source 1.8 -target 1.8

echo compiling UserDefSymbols...
echo ${JAVAC} ${VmOption} UserDefSymbols.java 
${JAVAC} ${VmOption} SimStAlgebraV8/UserDefSymbols.java -source 1.8 -target 1.8

echo compiling AlgebraV8AdhocFoaGetter...
echo ${JAVAC} ${VmOption} AlgebraV8AdhocFoaGetter.java
${JAVAC} ${VmOption} SimStAlgebraV8/AlgebraV8AdhocFoaGetter.java -source 1.8 -target 1.8

echo compiling AlgebraV8AdhocSelectionGetter...
echo ${JAVAC} ${VmOption} AlgebraV8AdhocSelectionGetter.java
${JAVAC} ${VmOption} SimStAlgebraV8/AlgebraV8AdhocSelectionGetter.java -source 1.8 -target 1.8

echo compiling AlgebraV8InputChecker...
echo ${JAVAC} ${VmOption} AlgebraV8InputChecker.java
${JAVAC} ${VmOption} SimStAlgebraV8/AlgebraV8InputChecker.java -source 1.8 -target 1.8


echo compiling AlgebraV8StartStateChecker...
echo ${JAVAC} ${VmOption} AlgebraV8StartStateChecker.java
${JAVAC} ${VmOption} SimStAlgebraV8/AlgebraV8StartStateChecker.java -source 1.8 -target 1.8

echo compiling AlgebraV8ResourceGetter...
echo ${JAVAC} ${VmOption} AlgebraV8ResourceGetter.java
${JAVAC} ${VmOption} SimStAlgebraV8/AlgebraV8ResourceGetter.java -source 1.8 -target 1.8

echo compiling AlgebraV8AdhocSkillNameGetter...
echo ${JAVAC} ${VmOption} AlgebraV8AdhocFoaGetter.java
${JAVAC} ${VmOption} SimStAlgebraV8/AlgebraV8AdhocSkillNameGetter.java -source 1.8 -target 1.8

echo compiling AlgebraV8AdhocSAIConverter...
echo ${JAVAC} ${VmOption} AlgebraV8AdhocSAIConverter.java
${JAVAC} ${VmOption} SimStAlgebraV8/AlgebraV8AdhocSAIConverter.java -source 1.8 -target 1.8

echo compiling AlgebraV8AdhocInterfaceElementGetter...
echo ${JAVAC} ${VmOption} AlgebraV8AdhocInterfaceElementGetter.java
${JAVAC} ${VmOption} SimStAlgebraV8/AlgebraV8AdhocInterfaceElementGetter.java -source 1.8 -target 1.8

echo compiling AlgebraV8AdhocQuizProblemAbstractor...
echo ${JAVAC} ${VmOption} AlgebraV8AdhocQuizProblemAbstractor.java
${JAVAC} ${VmOption} SimStAlgebraV8/AlgebraV8AdhocQuizProblemAbstractor.java -source 1.8 -target 1.8
