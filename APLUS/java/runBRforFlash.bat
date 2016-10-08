rem To enable debug logging to console, add "-debugCodes tsltsp tsltstp" at the end of the command line.
rem To enable tracing for scripting, add "-debugCodes tsltsp" at the end of the command line.

java  -cp "eclipseOutPut;Projects;lib/ctat.jar" edu.cmu.pact.BehaviorRecorder.Controller.CTAT_Launcher -Dschool_name="School1" -Dcourse_name="Course1" -Dunit_name="Unit1" -Dsection_name="Section1" %* -spEOM 00 -spUseSingleSocket true -spOneMsgPerSocket false -spMsgFormat M -spServerPort 1502 -debugCodes tsltsp -debugCodes tsltstp -debugCodes klc -debugCodes br >error.log 2>&1
pause
