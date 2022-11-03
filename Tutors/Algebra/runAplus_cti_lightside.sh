#!/bin/bash
./runAplus_lightside.sh &
LIGHTSIDE_PID=$!
function cleanup {
  kill $LIGHTSIDE_PID
  kill $(lsof -t -i:8000)
}
trap cleanup EXIT
./runAplus.sh -mtmc -cti -localLogging cti_study_2022
