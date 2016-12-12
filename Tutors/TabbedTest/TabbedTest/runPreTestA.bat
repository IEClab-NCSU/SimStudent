@echo off

javac -cp "./lib/DorminWidgets.jar" TabbedPreTestA.java

java -cp "./;./lib/DorminWidgets.jar" TabbedPreTestA -DProblemFileLocation=./A.brd -DBehaviorRecorderVisible=false -Dcourse_name=SimStPilotPreTestA.brd -Dschool_name=CMU