@echo off

javac -cp "./lib/DorminWidgets.jar" TabbedPreTest.java

java -cp "./;./lib/DorminWidgets.jar" TabbedPreTest -DProblemFileLocation=./%1 -DBehaviorRecorderVisible=false -Dcourse_name=SimStPilotPreTest%1 -Dschool_name=CMU