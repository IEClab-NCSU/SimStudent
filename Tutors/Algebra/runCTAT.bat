@echo off
set CVSDIR=%CD%
set CTATJAR=%CVSDIR%\lib\ctat.jar;%CVSDIR%\lib\jess.jar
set CPATH=%CTATJAR%;..;.
echo %CPATH%
set VmOptions=-cp %CPATH% -Xmx268435456  -Xss512K "-Dinstall4j.launcherId\=18" -Dinstall4j.swt\=false
set TutorArg=-traceLevel 5 -debugCodes br
echo command line arguments : %TutorArg%
set cmd=java %VmOptions% edu.cmu.pact.BehaviorRecorder.Controller.CTAT_Launcher  %TutorArg%
echo %cmd%
echo Starting the application
%cmd%