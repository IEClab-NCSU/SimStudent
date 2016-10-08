@echo off

javac -cp "./lib/DorminWidgets.jar;./swing-layout-1.0.3.jar" MslqQuestionnaire.java

java -cp "./;./lib/DorminWidgets.jar;./swing-layout-1.0.3.jar" MslqQuestionnaire -DProblemFileLocation=./MslqQuestionnaire.brd -DBehaviorRecorderVisible=false -Dcourse_name=SimStMslqQuestionnaire -Dschool_name=CMU