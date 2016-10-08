@echo off

javac -cp "../../lib/DorminWidgets.jar" TabbedPreTest.java

java -cp "../;../../lib/DorminWidgets.jar" TabbedPreTest.TabbedPreTest -DProblemFileLocation=TabbedPreTest/%1 -DBehaviorRecorderVisible=true -Dcourse_name=SimStPilotPreTest%1 -Dschool_name=CMU