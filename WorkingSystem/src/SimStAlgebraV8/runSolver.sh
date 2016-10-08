#!/bin/bash


JAVA="java"

JessJar="/Users/mazda/mazda-on-Mac/Project/SimStudent/CVS/SimStudent2/lib/Jess71p2/lib/jess.jar"
CtatJar="../../../../Tomodachi816/ctat.jar"
SimSt="../../../SimStudent2/bin"
UserDef="../UserDef"
CPATH="${SimSt}:${JessJar}:${CtatJar}:${UserDef}:..:."

VmOption="-classpath ${CPATH}"

echo ${JAVA} ${VmOption} SimStudent2.ProductionSystem.Solver
${JAVA} ${VmOption} SimStudent2.ProductionSystem.Solver
