#!/bin/bash
cd ..
LightsideDir=$(pwd)/lightside
cd $LightsideDir
sh ${LightsideDir}/scripts/prediction_server.sh
