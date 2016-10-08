#!/bin/bash

# StepAnalysis is included in the CommWidget.jar archive.
# Don't compile this module separately.  Use ant instead. 

echo StepAnalysis is included in the CommWidget.jar archive.
echo Don't compile this module separately.  Use ant instead. 

exit

echo ${OS}

if [ "${CVSDIR}" = "" ]; then
    if [ "${OS}" = "Windows_NT" ]; then
	CVSDir="f:/Project/CTAT/CVS-TREE"
    fi
    if [ "${OS}" != "Windows_NT" ]; then
	CVSDir="${HOME}/mazda-on-Mac/Project/CTAT/CVS-TREE"
    fi
fi

if [ "${OS}" = "Windows_NT" ]; then
    CPS=";"
fi
if [ "${OS}" != "Windows_NT" ]; then
    CPS=":"
fi

CommJar="${CVSDIR}/AuthoringTools/java/lib/CommWidgets.jar"
CPATH="${CommJar};..;."

# JAVAC="c:/UsrLocal/Java/jdk1.5.0_06/bin/javac.exe"
JAVAC="javac.exe"

VmOption="-cp ${CPATH}"

CMD="${JAVAC} ${VmOption} StepAnalysis.java"

echo compiling StepAnalysis.java...
echo ${CMD}
${CMD}
