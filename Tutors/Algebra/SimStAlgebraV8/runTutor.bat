@echo off
set DORMIN=..\lib\ctat.jar
set CPATH=%DORMIN%;..;.
set VmOption= -cp %CPATH% -DssFoilBase=..\FOIL6
set ARGS=-debugCodes miss rr -traceLevel 5
set ARGS=%ARGS% -ssRuleActivationTestMethod humanOracle
set ARGS=%ARGS% -ssHintMethod humanDemonstration
set ARGS=%ARGS% -ssFoaGetterClass SimStAlgebraV8.AlgebraV8AdhocFoaGetter
set ARGS=%ARGS% -ssProjectDir .
echo java %VmOption% SimStAlgebraV8.SimStAlgebraV8 %ARGS%
java %VmOption% SimStAlgebraV8.SimStAlgebraV8 %ARGS% >bat_log.txt
