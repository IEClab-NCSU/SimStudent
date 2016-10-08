set DorminJar=c:\pact-cvs-tree\AuthoringTools\java\lib\ctat.jar
set CPATH=%DorminJar%;.;..
set VmOption=-cp %CPATH%

echo javac %VmOption% TabbedTestA.java
javac %VmOption% TabbedTestA.java

echo java %VmOption% TabbedPreTest.TabbedTestA -DnoCtatWindow -DProblemFileLocation=./A2.brd
java %VmOption% TabbedPreTest.TabbedTestA -DnoCtatWindow -DProblemFileLocation=./A2.brd

pause 5
