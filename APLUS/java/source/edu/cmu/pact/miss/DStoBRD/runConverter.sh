#!/bin/bash

# Specify the directory where you have DirminWidgets.jar
#
CommJar="f:/Project/CTAT/CVS-TREE/AuthoringTools/java/lib/CommWidgets.jar"

# Java Class path
CPATH="${CommJar};."
VMarg="-cp $CPATH"

java ${VMarg} edu.cmu.pact.miss.DStoBRD.SimAlgebraDataConverter
