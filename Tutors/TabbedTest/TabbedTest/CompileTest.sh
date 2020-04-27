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



cmd="javac ${VmOption} TabbedTestC.java -source 1.8 -target 1.8";
echo $cmd;
${cmd};

cmd="javac ${VmOption} TabbedTestB.java -source 1.8 -target 1.8";
echo $cmd;
${cmd};

cmd="javac ${VmOption} TabbedTestA.java -source 1.8 -target 1.8";
echo $cmd;
${cmd};

cmd="javac ${VmOption} QuestionnaireDemographics.java -source 1.8 -target 1.8";
echo $cmd;
${cmd};

cmd="javac ${VmOption} QuestionnaireMT.java -source 1.8 -target 1.8";
echo $cmd;
${cmd};
