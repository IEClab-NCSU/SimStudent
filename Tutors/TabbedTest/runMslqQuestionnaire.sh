#!/bin/bash

if [ "${OS}" = "Windows_NT" ]; then
    SP=";"
fi
if [ "${OS}" != "Windows_NT" ]; then
    SP=":"
fi

javac -cp "./lib/DorminWidgets.jar${SP}swing-layout-1.0.3.jar" MslqQuestionnaire.java

java -cp "./${SP}./lib/DorminWidgets.jar${SP}swing-layout-1.0.3.jar" MslqQuestionnaire -DProblemFileLocation="./MslqQuestionnaire.brd" -DBehaviorRecorderVisible=false -Dcourse_name=SimStMslqQuestionnaire -Dschool_name=CMU