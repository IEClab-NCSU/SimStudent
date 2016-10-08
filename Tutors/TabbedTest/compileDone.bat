set DorminJar=c:\pact-cvs-tree\AuthoringTools\java\lib\ctat.jar
set CPATH=%DorminJar%;.;..
set VmOption=-cp %CPATH%

echo javac %VmOption% DoneButton.java
javac %VmOption% DoneButton.java

echo javac %VmOption% TabbedTest.java
javac %VmOption% TabbedTest.java
