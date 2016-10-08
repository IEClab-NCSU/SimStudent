#!/bin/bash

if [ "${OS}" = "Windows_NT" ]; then
    SP=";"
fi
if [ "${OS}" != "Windows_NT" ]; then
    SP=":"
fi

javac -cp "./lib/DorminWidgets.jar" TabbedPreTest.java

java -cp "./${SP}./lib/DorminWidgets.jar" TabbedPreTest -DProblemFileLocation="./$1" -DBehaviorRecorderVisible=false -Dcourse_name=SimStPilotPreTest%1 -Dschool_name=CMU