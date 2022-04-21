#!/bin/bash
cd ..
LightsideDir=$(pwd)/bazaar/lightside
cd $LightsideDir
sh ${LightsideDir}/scripts/prediction_server.sh
