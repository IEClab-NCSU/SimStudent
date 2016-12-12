#!/bin/bash

if [ "${OS}" = "Windows_NT" ]; then
    SP=";"
fi
if [ "${OS}" != "Windows_NT" ]; then
    SP=":"
fi

javac -cp "./lib/DorminWidgets.jar" TabbedPreTestA.java

java -cp "./${SP}./lib/DorminWidgets.jar" TabbedPreTestA -DProblemFileLocation="./A.brd" -DBehaviorRecorderVisible=false -Dcourse_name=SimStPilotPreTestA.brd -Dschool_name=CMU