@echo off
set CVSDIR=%CD%
cd SimStAlgebraV8
set CTATJAR=%CVSDIR%\lib\ctat.jar;%CVSDIR%\lib\jess.jar
set CPATH=%CTATJAR%;..;.
echo %CPATH%
set VmOptions=-cp %CPATH% -Xmx512m -DssFoilBase=../FOIL6
set TutorArg=-traceLevel 3 -debugCodes miss ss
set TutorArg=%TutorArg% -ssProjectDir .
set TutorArg=%TutorArg% -br
set TutorArg=%TutorArg% -ssSearchTimeOutDuration 60000
echo command line arguments : %TutorArg%
set cmd=java %VmOptions% SimStAlgebraV8.SimStAlgebraV8 %TutorArg%
%cmd%