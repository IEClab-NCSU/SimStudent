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
echo ${JAVAC} ${VmOption} SimStAlgebraV8.java
${JAVAC} ${VmOption} SimStAlgebraV8.java

echo compiling UserDefSymbols...
echo ${JAVAC} ${VmOption} UserDefSymbols.java
${JAVAC} ${VmOption} UserDefSymbols.java

echo compiling AlgebraV8AdhocFoaGetter...
echo ${JAVAC} ${VmOption} AlgebraV8AdhocFoaGetter.java
${JAVAC} ${VmOption} AlgebraV8AdhocFoaGetter.java

echo compiling AlgebraV8AdhocSelectionGetter...
echo ${JAVAC} ${VmOption} AlgebraV8AdhocSelectionGetter.java
${JAVAC} ${VmOption} AlgebraV8AdhocSelectionGetter.java

echo compiling AlgebraV8InputChecker...
echo ${JAVAC} ${VmOption} AlgebraV8InputChecker.java
${JAVAC} ${VmOption} AlgebraV8InputChecker.java

echo compiling AlgebraV8AdhocSkillNameGetter...
echo ${JAVAC} ${VmOption} AlgebraV8AdhocFoaGetter.java
${JAVAC} ${VmOption} AlgebraV8AdhocSkillNameGetter.java

echo compiling AlgebraV8AdhocSAIConverter...
echo ${JAVAC} ${VmOption} AlgebraV8AdhocSAIConverter.java
${JAVAC} ${VmOption} AlgebraV8AdhocSAIConverter.java

echo compiling AlgebraV8AdhocInterfaceElementGetter...
echo ${JAVAC} ${VmOption} AlgebraV8AdhocInterfaceElementGetter.java
${JAVAC} ${VmOption} AlgebraV8AdhocInterfaceElementGetter.java

echo compiling AlgebraV8AdhocQuizProblemAbstractor...
echo ${JAVAC} ${VmOption} AlgebraV8AdhocQuizProblemAbstractor.java
${JAVAC} ${VmOption} AlgebraV8AdhocQuizProblemAbstractor.java
