@echo off

javac -cp "./lib/DorminWidgets.jar" TabbedPreTestB.java

java -cp "./;./lib/DorminWidgets.jar" TabbedPreTestB -DProblemFileLocation=./B.brd -DBehaviorRecorderVisible=false -Dcourse_name=SimStPilotPreTestB.brd -Dschool_name=CMU