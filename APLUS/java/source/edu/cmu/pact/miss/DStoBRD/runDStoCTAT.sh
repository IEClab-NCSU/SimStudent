#!/bin/bash

# Specify the directory where you have DirminWidgets.jar
#
if [ "${CVSDIR}" = "" ]; then
    if [ "${OS}" = "Windows_NT" ]; then
	CVSDIR="w:/mazda-on-Mac/Project/CTAT/CVS-TREE";
    fi
    if [ "${OS}" != "Windows_NT" ]; then
	CVSDIR="${HOME}/mazda-on-Mac/Project/CTAT/CVS-TREE";
    fi
fi

if [ "${OS}" = "Windows_NT" ]; then
    CPS=";"
fi    
if [ "${OS}" != "Windows_NT" ]; then
    CPS=":"
fi    
CommJar="${CVSDIR}/AuthoringTools/java/lib/CommWidgets.jar"

# Java Class path
CPATH="${CommJar}${CPS}."
VMarg="-cp $CPATH"

DSFILE=$1
CTATFILE=$2

echo java ${VMarg} edu.cmu.pact.miss.DStoBRD.DStoCTAT ${DSFILE} ${CTATFILE}
java ${VMarg} edu.cmu.pact.miss.DStoBRD.DStoCTAT ${DSFILE} ${CTATFILE}
