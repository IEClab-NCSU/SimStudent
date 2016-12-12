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

CtatJar="${CVSDIR}/AuthoringTools_3_1/java/lib/ctat.jar"


if [ "${OS}" = "Windows_NT" ]; then
    CPATH="${CtatJar};..;."
fi
if [ "${OS}" != "Windows_NT" ]; then
    CPATH="${CtatJar}:..:."
fi


# VmOption="-cp ${CPATH} -DnoCtatWindow -Xmx512m"
if [ -z $br ];
then
VmOption="-cp ${CPATH}";
else
VmOption="-cp ${CPATH}"
fi

##java ${VmOption} TabbedPreTestA -DProblemFileLocation="./A.brd" -DBehaviorRecorderVisible=false -Dcourse_name=SimStPilotPreTestA.brd -Dschool_name=CMU

#cmd="javac -cp "${CtatJar}" TabbedTestA.java"
echo $cmd;	
#java "${CtatJar}" TabbedTestA -DProblemFileLocation="./A2.brd" -DBehaviorRecorderVisible=false -Dcourse_name=SimStPilotPreTestA.brd -Dschool_name=CMU



cmd="javac ${VmOption} QuestionnaireMT.java -source 1.6 -target 1.6";
${cmd};


cmd="java ${VmOption} TabbedTest.QuestionnaireMT -DProblemFileLocation=./questionnaireBL.brd -ssLocalLogging";
${cmd};


