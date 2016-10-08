set DorminJar=c:\pact-cvs-tree\AuthoringTools\java\lib\ctat.jar
set CPATH=%DorminJar%;.;..
set VmOption=-cp %CPATH%

echo javac %VmOption% TabbedTestC.java
javac %VmOption% TabbedTestC.java

echo java %VmOption% TabbedPreTest.TabbedTestC -DnoCtatWindow -DProblemFileLocation=./C2.brd
java %VmOption% TabbedPreTest.TabbedTestC -DnoCtatWindow -DProblemFileLocation=./C2.brd

pause 5
