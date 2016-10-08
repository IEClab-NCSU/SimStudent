set DorminJar=c:\pact-cvs-tree\AuthoringTools\java\lib\ctat.jar
set CPATH=%DorminJar%;.;..
set VmOption=-cp %CPATH%

echo javac %VmOption% QuestionnaireMT.java
javac %VmOption% QuestionnaireMT.java

echo java %VmOption% TabbedPreTest.QuestionnaireMT -DnoCtatWindow -DProblemFileLocation=./questionnaireMT.brd
java %VmOption% TabbedPreTest.QuestionnaireMT -DnoCtatWindow -DProblemFileLocation=./questionnaireMT.brd

pause 5
