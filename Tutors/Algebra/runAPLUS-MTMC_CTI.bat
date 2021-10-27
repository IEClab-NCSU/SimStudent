@echo off
:: To set the dataset name for the logs, assign a value to the variable 'DatasetName'
set DatasetName=Logging2017
set CVSDIR=%CD%
cd SimStAlgebraV8
set ProjectDir=%CVSDIR%\SimStAlgebraV8
set CTATJAR=%CVSDIR%\lib\ctat.jar;%CVSDIR%\lib\jess.jar
set CPATH="%CTATJAR%;..;."

if "%DatasetName%" == "" (
    echo.
    echo ***********************************WARNING************************************
    echo ***********************************WARNING************************************
    echo ***********************************WARNING************************************
    echo *									      *
    echo * Provide a name for the dataset.					      *
    echo * To do this, assign a name to the variable 'DatasetName'		      *
    echo * For example, if you want to assign 'Logging2016' as a dataset name	      *
    echo * then rewrite the following at line 3 in runAPLUS-MTMC.bat		      *
    echo *            set DatasetName=					              *
    echo *	      as							      *
    echo *            set DatasetName=Logging2016				      *
    echo *									      *
    echo ******************************************************************************
    echo.
    pause
    goto :eof
)

set VmOptions= -cp %CPATH% -Xmx512m -DssFoilBase=../FOIL6 -XX:+UnlockCommercialFeatures -XX:+FlightRecorder
set ARGS=-ssMetaTutorMode -ssMetaTutorModeLevel MetaCognitive
set TutorArg=-traceLevel 3 -debugCodes mt1 
set TutorArg=%TutorArg% -ssRunInPLE 
set TutorArg=%TutorArg% -ssSelfExplainMode
set TutorArg=%TutorArg% -ssRuleActivationTestMethod humanOracle -ssHintMethod humanDemonstration
set TutorArg=%TutorArg% -ssProblemCheckerOracle ClOracle
set TutorArg=%TutorArg% -ssIntroVideo metatutor.mov -ssOverviewPage curriculum.html
set TutorArg=%TutorArg% -ssLoadPrefsFile brPrefsStacy.xml
set TutorArg=%TutorArg% -ssLogFolder C:\Users\Public\SimStAlgebraV9\log\APLUS
set TutorArg=%TutorArg% -ssCacheOracleInquiry false
set TutorArg=%TutorArg% -ssConstructiveTuteeInquiryFTIMode -ssCTIBothStuckParams none
set TutorArg=%TutorArg% -ssLocalLogging true
set TutorArg=%TutorArg% -ssSearchTimeOutDuration 20000
set TutorArg=%TutorArg% -ssTutorServerTimeOutDuration 100000
set TutorArg=%TutorArg% -ssMaxSearchDepth 3
set TutorArg=%TutorArg% -ssSkillNameGetterClass SimStAlgebraV8.AlgebraV8AdhocSkillNameGetter
set TutorArg=%TutorArg% -ssNearSimilarProblemsGetterClass SimStAlgebraV8.NearSimilarEquationFinder
set TutorArg=%TutorArg% -ssSetInactiveInterfaceTimeout 5000000
set TutorArg=%TutorArg% -ssInputCheckerClass SimStAlgebraV8.AlgebraV8InputChecker
set TutorArg=%TutorArg% -ssStartStateCheckerClass SimStAlgebraV8.AlgebraV8StartStateChecker
set TutorArg=%TutorArg% -ssFoaGetterClass SimStAlgebraV8.AlgebraV8AdhocFoaGetter
set TutorArg=%TutorArg% -ssSelectionOrderGetterClass SimStAlgebraV8.AlgebraV8AdhocSelectionGetter
set TutorArg=%TutorArg% -ssClSolverTutorSAIConverter SimStAlgebraV8.AlgebraV8AdhocSAIConverter
set TutorArg=%TutorArg% -ssActivationList AccuracySortedActivationList
set TutorArg=%TutorArg% -ssFoaClickDisabled
set TutorArg=%TutorArg% -ssMetaTutorMode
set TutorArg=%TutorArg% -ssQuizProblemAbstractor SimStAlgebraV8.AlgebraV8AdhocQuizProblemAbstractor
set TutorArg=%TutorArg% -ssCLQuizReqMode
set TutorArg=%TutorArg% -ssProjectDirectory %ProjectDir%
set TutorArg=%TutorArg% -ssNumBadInputRetries 2
set TutorArg=%TutorArg% -ssProblemsPerQuizSection 2
set TutorArg=%TutorArg% -ssOperatorFile operators.txt
set TutorArg=%TutorArg% -Dcourse_name=%DatasetName%
set TutorArg=%TutorArg% -Dschool_name=someSchool
set TutorArg=%TutorArg% -Dclass_name=someClass
set TutorArg=%TutorArg% -ssCondition MetaTutorMetaCognitiveHints
set TutorArg=%TutorArg% %ARGS%

echo command line arguments : %TutorArg%

set cmd=java %VmOptions% SimStAlgebraV8.SimStAlgebraV8 %TutorArg%
echo %cmd%
echo Starting the application
%cmd%
