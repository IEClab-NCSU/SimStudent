#!/bin/bash

if [ "${CVSDIR}" = "" ]
then
    if [ "${OS}" = "Windows_NT" ]; then
	CVSDIR="c:/pact-cvs-tree"
	CPS=";"
    fi
    if [ "${OS}" != "Windows_NT" ]; then
	CVSDIR="${HOME}/Desktop/SimStudentGithub/SimStudent-master"
	CPS=":"
    fi
fi

CtatJar="${CVSDIR}/APLUS/java/lib/ctat.jar"


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
#echo $cmd;	
#java "${CtatJar}" TabbedTestA -DProblemFileLocation="./A2.brd" -DBehaviorRecorderVisible=false -Dcourse_name=SimStPilotPreTestA.brd -Dschool_name=CMU



cmd="javac ${VmOption} TabbedTestA.java TestCheckBox.java TestDoneButton.java -source 1.8 -target 1.8";
echo $cmd;
${cmd};


#cmd="java ${VmOption} TabbedTest.TabbedTestA -ssLocalLogging -Dcourse_name=simStPilotJanuary-TestScores -DnoCtatWindow -DProblemFileLocation=./A2.brd";
#${cmd};

#cmd="java ${VmOption} TabbedTest.TabbedTestA -DDebugCodes=log -ssLoadPrefsFile brPrefsTest.xml -ssUserID Dwrrww -ssLocalLogging -ssLogging -ssLogURL http://172.17.4.1:1502/log/server -Dcourse_name=simstOnlineTest-TestScores -DProblemFileLocation=./A2.brd";
#${cmd};




