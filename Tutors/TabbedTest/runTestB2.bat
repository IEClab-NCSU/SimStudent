set DorminJar=c:\pact-cvs-tree\AuthoringTools\java\lib\ctat.jar
set CPATH=%DorminJar%;.;..
set VmOption=-cp %CPATH%

echo javac %VmOption% TabbedTestB.java
javac %VmOption% TabbedTestB.java

echo java %VmOption% TabbedPreTest.TabbedTestB -DnoCtatWindow -DProblemFileLocation=./B2.brd
java %VmOption% TabbedPreTest.TabbedTestB -DnoCtatWindow -DProblemFileLocation=./B2.brd

pause 5
