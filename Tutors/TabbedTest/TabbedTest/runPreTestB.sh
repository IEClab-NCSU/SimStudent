#!/bin/bash

if [ "${OS}" = "Windows_NT" ]; then
    SP=";"
fi
if [ "${OS}" != "Windows_NT" ]; then
    SP=":"
fi

javac -cp "./lib/DorminWidgets.jar" TabbedPreTestB.java

java -cp "./${SP}./lib/DorminWidgets.jar" TabbedPreTestB -DProblemFileLocation="./B.brd" -DBehaviorRecorderVisible=false -Dcourse_name=SimStPilotPreTestB.brd -Dschool_name=CMU