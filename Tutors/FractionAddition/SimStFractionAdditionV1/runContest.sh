#!/bin/bash

echo ${OS}

# Class path
# 
if [ "${CVSDIR}" = "" ]
then
    if [ "${OS}" = "Windows_NT" ]; then
	CVSDIR="f:/pact-cvs-tree"
    fi
    if [ "${OS}" != "Windows_NT" ]; then
	CVSDIR="${HOME}/mazda-on-Mac/Project/CTAT/CVS-TREE"
    fi
fi

CommJar="${CVSDIR}/AuthoringTools/java/lib/ctat.jar"

if [ "${OS}" = "Windows_NT" ]; then
    CPATH="${CommJar};..;."
fi
if [ "${OS}" != "Windows_NT" ]; then
    CPATH="${CommJar}:..:."
fi

VmOption="-cp ${CPATH} -Xmx512m"
cmd="java ${VmOption} edu/cmu/pact/miss/PeerLearning/GameShow/ContestServer"
echo $cmd $*
$cmd $*
