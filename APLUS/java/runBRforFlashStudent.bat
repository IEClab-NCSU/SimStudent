@echo .
@echo %0: First argument is .brd file's path, relative to main CTAT dir

if "%1"=="" exit /b -1
if not exist "%1" exit /b -1

rem To enable debug logging to console, add "-debugCodes sp" at the end of the command line.

java  -cp "Projects;lib/ctat.jar" edu.cmu.pact.BehaviorRecorder.Controller.CTAT_Launcher -Dschool_name="School1" -Dcourse_name="Course1" -Dunit_name="Unit1" -Dsection_name="Section1" -DBehaviorRecorderMode=Tutor -DBehaviorRecorderVisible=false -DProblemFileLocation="%1" -noGraph -spEOM 00 -spUseSingleSocket true -spOneMsgPerSocket false -spMsgFormat M -spServerPort 1502 -debugCodes sp
