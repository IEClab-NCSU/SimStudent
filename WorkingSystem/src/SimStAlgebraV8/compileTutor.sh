#!/bin/bash


JAVAC="javac"

JessJar="/Users/mazda/mazda-on-Mac/Project/SimStudent/CVS/Jess71p2/lib/jess.jar"
CtatJar="../../../../Tomodachi816/ctat.jar"
CPATH="${JessJar}:${CtatJar}:..:."

VmOption="-classpath ${CPATH}"

echo compiling UserDefSymbols...
echo ${JAVAC} ${VmOption} UserDefSymbols.java
${JAVAC} ${VmOption} UserDefSymbols.java

exit

echo compiling Tutor interface...
echo ${JAVAC} ${VmOption} SimStAlgebraV8.java
${JAVAC} ${VmOption} SimStAlgebraV8.java

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
