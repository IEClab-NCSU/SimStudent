#!/bin/bash
./runAplus_lightside.sh &
LIGHTSIDE_PID=$!
function cleanup {
  kill $LIGHTSIDE_PID
}
trap cleanup EXIT
./runAplus.sh -mtmc -cti -localLogging cti_study
