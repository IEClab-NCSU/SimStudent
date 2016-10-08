#!/bin/bash

# Class path
# 
if [ "${CVSDIR}" = "" ]
then
    if [ "${OS}" = "Windows_NT" ]; then
        CVSDIR="f:/pact-cvs-tree"
    fi
    if [ "${OS}" != "Windows_NT" ]; then
        CVSDIR="${HOME}/intern"
    fi
fi

AllLibs="${CVSDIR}/AuthoringTools/java/lib"

cp ${AllLibs}/* . -R

