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

echo $VmOption
##java ${VmOption} TabbedPreTestA -DProblemFileLocation="./A.brd" -DBehaviorRecorderVisible=false -Dcourse_name=SimStPilotPreTestA.brd -Dschool_name=CMU

#cmd="javac -cp "${CtatJar}" TabbedTestA.java"
#echo $cmd;	
#java "${CtatJar}" TabbedTestA -DProblemFileLocation="./A2.brd" -DBehaviorRecorderVisible=false -Dcourse_name=SimStPilotPreTestA.brd -Dschool_name=CMU



cmd="javac ${VmOption} QuestionnaireDemographics.java -source 1.8 -target 1.8";
echo $cmd;
${cmd};

#cmd="javac ${VmOption} TabbedTestB.java -source 1.8 -target 1.8";
#echo $cmd;
#${cmd};

#cmd="javac ${VmOption} TabbedTestA.java -source 1.8 -target 1.8";
#echo $cmd;
#${cmd};

#cmd="java ${VmOption} TabbedTest.TabbedTestA -ssLocalLogging -Dcourse_name=simStPilotJanuary-TestScores -DnoCtatWindow -DProblemFileLocation=./A2.brd";
#${cmd};

cmd="java ${VmOption} TabbedTest.QuestionnaireDemographics -traceLevel 3 -debugCodes miss log -ssProjectDirectory /Users/simstudent/Desktop/SimStudentGithub/SimStudent-master/Tutors/TabbedTest -ssLoadPrefsFile brPrefsTest.xml -ssUserID NIKOLAOS3245 -ssLocalLogging  -ssLogging -ssLogURL http://pslc-qa.andrew.cmu.edu/log/server -Dcourse_name=simstOnlineTest-TestScores -Dunit_name=Demo -DnoCtatWindow -DProblemFileLocation=./demog1.brd";
${cmd};
