#!/bin/bash
cd ..
LightsideDir=$(pwd)/LightSide/lightside
cd $LightsideDir
sh ${LightsideDir}/scripts/prediction_server.sh
