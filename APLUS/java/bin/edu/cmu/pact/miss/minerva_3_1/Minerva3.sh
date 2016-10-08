#!/bin/bash
#
# This script is to run Minerva3 created by Andrew Lee to extract
# Error and Problem Schema out of log-data
#
# (c) Noboru Matsuda, 2009
# Carnegie Mellon University

if [ "${OS}" = "Windows_NT" ]; then
    SP=";"
fi
if [ "${OS}" != "Windows_NT" ]; then
    SP=":"
fi

JAVA=java

$JAVA -cp ".${SP}bin" Minerva3 $1 $2
