@echo off
setlocal EnableDelayedExpansion
cd SimStAlgebraV8
set CVSDIR=%~dp0
set CTATJAR=%CVSDIR%lib\ctat.jar;%CVSDIR%lib\jess.jar
set CPATH=%CTATJAR%;..;.
echo %CPATH%
set redir=off
set br=off
set ARGS=
set outstr=
set mode=
set PLE=on
set TutorArg=-traceLevel 3 -debugCodes miss cogTutor ss sswme 
for %%a in (%*) do (
        set param=%%a
	echo !param!
	if "!redir!" equ "on" (
        	if [!outstr!] == [] (
		    set outstr= !param!
                ) else (
                     set ARGS=!ARGS! !param!
                )
        ) else (
		if !redir! equ "off" (
                     set ARGS=!ARGS! !param!
                 )
        )
	if "!param!" equ "-noPle" (
		set PLE=off
   	)	 
   	if "!param!" equ "-noSe" (
          	set SE=off
	)
	if "!param!" equ "-mt" (
             set ARGS=!ARGS! -ssMetaTutorMode
             set mode=-mt
	)
	if "!param!" equ "-ct" (
	 	 set ARGS=!ARGS! -ssCogTutorMode
                 set mode=-ct 
	)
	if "!param!" equ "-cta" (
		  set ARGS=!ARGS! -ssAplusCtrlCogTutorMode
                  set mode=-cta
	)
	if "!param!" equ "-tt" (
	  	set ARGS=!ARGS! -ssTutalkParams none
	)
  	if "!param!" equ "-u" (
        	  set ARGS=!ARGS! -ssUserID
	)
  	if "!param!" equ "-o" (
                 set redir=on
	)
	if "!param!" equ "-br" (
	 	 set br=on
	)
  	if "!param!" equ "-help" (
		echo -noPle - turns PLE off
		echo "	-noSe - turn Self Explanation off"
		echo "	-mt - turns metatutor on"
		echo "	-mtc - turns metatutor on with cognitive hint only"
		echo "	-mtmc - turns metatutor on with meta-cognitive hint only"
		echo "	-cta - launch APLUS in AplusControl mode"
		echo "	-ct - launch APLUS in Cognitive Tutor mode"
		echo "	-tt - turns Tutalk on"
		echo "	-br - displays Behavior Recorder window"
		echo "	-u or -user <name> - sets the user ID"
		echo "	-o or -output <filename> - redirects output to file"

       )
       
)

echo Status of BR !br!
if "!br!" equ "off" (
   set VmOptions= -cp %CPATH% -Xmx512m -DssFoilBase=../FOIL6
) else (
   set VmOptions= -cp %CPATH% -Xmx512m -DssFoilBase=../FOIL6 -XX:+UnlockCommercialFeatures -XX:+FlightRecorder
)


if "!PLE!" equ "on" (
    set TutorArg=!TutorArg! -ssRunInPLE
)
if "!SE!" equ "on" (
    set TutorArg=!TutorArg! -ssSelfExplainMode
)
echo First argument : %1%
set mode=%1%
echo Mode !mode!
if "!mode!" equ "-ct" (
   set TutorArg=!TutorArg! -ssHintMethod builtInClSolverTutor -ssRuleActivationTestMethod builtInClSolverTutor
)
if "!mode!" equ "-cta" (
   set TutorArg=!TutorArg! -ssHintMethod builtInClSolverTutor -ssRuleActivationTestMethod builtInClSolverTutor
) else (
    set TutorArg=!TutorArg! -ssRuleActivationTestMethod humanOracle -ssHintMethod humanDemonstration
) 
  
if "!mode!" equ "-mtc" (
    set TutorArg=!TutorArg! -ssMetaTutorMode -ssMetaTutorModeLevel Cognitive
) else (
	if "!mode!" equ "-mtmc" (
          set TutorArg=!TutorArg! -ssMetaTutorMode -ssMetaTutorModeLevel MetaCognitive
    )
)

set TutorArg=!TutorArg! -ssProjectDir %CVSDIR%
set TutorArg=!TutorArg! -ssOverviewPage curriculum.html
set TutorArg=!TutorArg! -ssLoadPrefsFile brPrefsStacy.xml
set TutorArg=!TutorArg! -ssCacheOracleInquiry false
set TutorArg=!TutorArg! -ssLocalLogging true
set TutorArg=!TutorArg! -ssSearchTimeOutDuration 20000
set TutorArg=!TutorArg! -ssTutorServerTimeOutDuration 100000
set TutorArg=!TutorArg! -ssMaxSearchDepth 3
set TutorArg=!TutorArg! -ssSkillNameGetterClass SimStAlgebraV8.AlgebraV8AdhocSkillNameGetter
set TutorArg=!TutorArg! -ssSetInactiveInterfaceTimeout 5000000
set TutorArg=!TutorArg! -ssInputCheckerClass SimStAlgebraV8.AlgebraV8InputChecker
set TutorArg=!TutorArg! -ssStartStateCheckerClass SimStAlgebraV8.AlgebraV8StartStateChecker
set TutorArg=!TutorArg! -ssFoaGetterClass SimStAlgebraV8.AlgebraV8AdhocFoaGetter
set TutorArg=!TutorArg! -ssSelectionOrderGetterClass SimStAlgebraV8.AlgebraV8AdhocSelectionGetter
set TutorArg=!TutorArg! -ssClSolverTutorSAIConverter SimStAlgebraV8.AlgebraV8AdhocSAIConverter
set TutorArg=!TutorArg! -ssActivationList AccuracySortedActivationList
set TutorArg=!TutorArg! -ssFoaClickDisabled
set TutorArg=!TutorArg! -ssNumBadInputRetries 2
set TutorArg=!TutorArg! -ssProblemsPerQuizSection 2
set TutorArg=!TutorArg! -Dcourse_name=SimStudent_StudyVII_Preparation
set TutorArg=!TutorArg! -Dschool_name=someSchool
set TutorArg=!TutorArg! -Dclass_name=someClass
set TutorArg=!TutorArg! -ssCondition devTesting
set TutorArg=!TutorArg! !ARGS!
echo command line arguments : !TutorArg!

set cmd=java !VmOptions! SimStAlgebraV8.SimStAlgebraV8 !TutorArg!
echo !cmd!
echo Starting the application
if "!redir!" equ "on" (
 echo !echo!
   
)else (
  !cmd!
)

