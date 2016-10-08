#!/bin/bash
cd ../..
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
echo ${JAVAC} ${VmOption} TermGrabber.java -source 1.8 -target 1.8
${JAVAC} ${VmOption} SimStAlgebraV8/LucyWeakPK/TermGrabber.java -source 1.8 -target 1.8


echo compiling GetFirstInteger...
echo ${JAVAC} ${VmOption} GetFirstInteger.java
${JAVAC} ${VmOption} SimStAlgebraV8/LucyWeakPK/GetFirstInteger.java -source 1.8 -target 1.8


echo compiling GetFirstIntegerBeforeLetter...
echo ${JAVAC} ${VmOption} GetFirstIntegerBeforeLetter.java
${JAVAC} ${VmOption} SimStAlgebraV8/LucyWeakPK/GetFirstIntegerBeforeLetter.java -source 1.8 -target 1.8

echo compiling GetFirstIntegerBeforeLetterWithoutSign...
echo ${JAVAC} ${VmOption} GetFirstIntegerBeforeLetterWithoutSign.java
${JAVAC} ${VmOption} SimStAlgebraV8/LucyWeakPK/GetFirstIntegerBeforeLetterWithoutSign.java -source 1.8 -target 1.8


echo compiling GetFirstIntegerWithoutSign...
echo ${JAVAC} ${VmOption} GetFirstIntegerWithoutSign.java
${JAVAC} ${VmOption} SimStAlgebraV8/LucyWeakPK/GetFirstIntegerWithoutSign.java -source 1.8 -target 1.8


echo compiling GetFirstNearestInteger...
echo ${JAVAC} ${VmOption} GetFirstNearestInteger.java
${JAVAC} ${VmOption} SimStAlgebraV8/LucyWeakPK/GetFirstNearestInteger.java -source 1.8 -target 1.8


echo compiling GetFirstNearestIntegerWithoutSign...
echo ${JAVAC} ${VmOption} GetFirstNearestIntegerWithoutSign.java
${JAVAC} ${VmOption} SimStAlgebraV8/LucyWeakPK/GetFirstNearestIntegerWithoutSign.java -source 1.8 -target 1.8


echo compiling GetSecondInteger...
echo ${JAVAC} ${VmOption} GetSecondInteger.java
${JAVAC} ${VmOption} SimStAlgebraV8/LucyWeakPK/GetSecondInteger.java -source 1.8 -target 1.8


echo compiling GetSecondIntegerBeforeLetter...
echo ${JAVAC} ${VmOption} GetSecondIntegerBeforeLetter.java
${JAVAC} ${VmOption} SimStAlgebraV8/LucyWeakPK/GetSecondIntegerBeforeLetter.java -source 1.8 -target 1.8



echo compiling GetSecondIntegerBeforeLetterWithoutSign...
echo ${JAVAC} ${VmOption} GetSecondIntegerBeforeLetterWithoutSign.java
${JAVAC} ${VmOption} SimStAlgebraV8/LucyWeakPK/GetSecondIntegerBeforeLetterWithoutSign.java -source 1.8 -target 1.8





echo compiling GetSecondIntegerWithoutSign...
echo ${JAVAC} ${VmOption} GetSecondIntegerWithoutSign.java
${JAVAC} ${VmOption} SimStAlgebraV8/LucyWeakPK/GetSecondIntegerWithoutSign.java -source 1.8 -target 1.8


echo compiling GetSecondNearestInteger...
echo ${JAVAC} ${VmOption} GetSecondNearestInteger.java
${JAVAC} ${VmOption} SimStAlgebraV8/LucyWeakPK/GetSecondNearestInteger.java -source 1.8 -target 1.8



echo compiling GetSecondNearestIntegerWithoutSign...
echo ${JAVAC} ${VmOption} GetSecondNearestIntegerWithoutSign.java
${JAVAC} ${VmOption} SimStAlgebraV8/LucyWeakPK/GetSecondNearestIntegerWithoutSign.java -source 1.8 -target 1.8






