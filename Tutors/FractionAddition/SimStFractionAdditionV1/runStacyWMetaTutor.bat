set DorminJar=..\lib\ctat.jar
set CPATH=%DorminJar%;..;.
set VmOption=-DDebugCodes=miss,ss,rr -cp %CPATH% -Xmx512m -DssFoilBase=..\FOIL6
set TutorArg=-traceLevel 5 -debugCodes miss,ss,rr
set TutorArg=%TutorArg% -ssRunInPLE
set TutorArg=%TutorArg% -ssLoadPrefsFile brPrefsStacy.xml
set TutorArg=%TutorArg% -ssSelfExplainMode
set TutorArg=%TutorArg% -ssIntroVideo metatutor-tutorial.mov
REM set TutorArg=%TutorArg% -ssLogging
set TutorArg=%TutorArg% -ssLocalLogging
set TutorArg=%TutorArg% -ssUserID TestSS
set TutorArg=%TutorArg% -ssOverviewPage curriculum.html
set TutorArg=%TutorArg% -ssLogURL http://pslc-qa.andrew.cmu.edu/log/server
set TutorArg=%TutorArg% -ssCacheOracleInquiry false
set TutorArg=%TutorArg% -ssSearchTimeOutDuration 20000
set TutorArg=%TutorArg% -ssMaxSearchDepth 3
set TutorArg=%TutorArg% -ssSkillNameGetterClass SimStAlgebraV8.AlgebraV8AdhocSkillNameGetter
set TutorArg=%TutorArg% -ssRuleActivationTestMethod humanOracle
set TutorArg=%TutorArg% -ssHintMethod humanDemonstration
set TutorArg=%TutorArg% -ssInputMatcher edu.cmu.pact.miss.userDef.algebra.IsEquivalent
set TutorArg=%TutorArg% -ssInputCheckerClass SimStAlgebraV8.AlgebraV8InputChecker
set TutorArg=%TutorArg% -ssFoaGetterClass SimStAlgebraV8.AlgebraV8AdhocFoaGetter
set TutorArg=%TutorArg% -ssSelectionOrderGetterClass SimStAlgebraV8.AlgebraV8AdhocSelectionGetter
set TutorArg=%TutorArg% -ssClSolverTutorSAIConverter SimStAlgebraV8.AlgebraV8AdhocSAIConverter
set TutorArg=%TutorArg% -ssActivationList AccuracySortedActivationList
set TutorArg=%TutorArg% -ssFoaClickDisabled
set TutorArg=%TutorArg% -ssMetaTutorMode
set TutorArg=%TutorArg% -ssQuizProblemAbstractor SimStAlgebraV8.AlgebraV8AdhocQuizProblemAbstractor
set TutorArg=%TutorArg% -ssCLQuizReqMode
set TutorArg=%TutorArg% -ssProjectDir .
set TutorArg=%TutorArg% -ssNumBadInputRetries 2
set TutorArg=%TutorArg% -ssProblemsPerQuizSection 2
set TutorArg=%TutorArg% -ssOperatorFile operators.txt
set TutorArg=%TutorArg% -Dcourse_name=simStMetaTutor -Dschool=pilot -Dunit_name=Tutoring



echo java %VmOption% SimStAlgebraV8.SimStAlgebraV8 %TutorArg%

java %VmOption% SimStAlgebraV8.SimStAlgebraV8 %TutorArg% 1>bat_log.txt
echo %ERRORLEVEL%
exit %ERRORLEVEL%