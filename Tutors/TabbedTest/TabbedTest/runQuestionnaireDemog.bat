set DorminJar=c:\pact-cvs-tree\AuthoringTools\java\lib\ctat.jar
set CPATH=%DorminJar%;.;..
set VmOption=-cp %CPATH%

echo javac %VmOption% QuestionnaireDemog.java
javac %VmOption% QuestionnaireDemog.java

echo java %VmOption% TabbedPreTest.QuestionnaireDemog -DnoCtatWindow -DProblemFileLocation=./demog.brd
java %VmOption% TabbedPreTest.QuestionnaireDemog -DnoCtatWindow -DProblemFileLocation=./demog.brd

pause 5
