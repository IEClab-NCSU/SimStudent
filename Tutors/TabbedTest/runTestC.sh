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



cmd="javac ${VmOption} TabbedTestC.java -source 1.6 -target 1.6";
echo $cmd;
${cmd};


#cmd="java ${VmOption} TabbedTest.TabbedTestA -ssLocalLogging -Dcourse_name=simStPilotJanuary-TestScores -DnoCtatWindow -DProblemFileLocation=./A2.brd";
#${cmd};

cmd="java ${VmOption} TabbedTest.TabbedTestC -ssLoadPrefsFile brPrefsTest.xml -ssUserID NIKOLAOS3245 -ssLocalLogging  -ssLogging -ssLogURL http://pslc-qa.andrew.cmu.edu/log/server -Dcourse_name=simstOnlineTest-TestScores -DnoCtatWindow -DProblemFileLocation=./C2.brd";
${cmd};
