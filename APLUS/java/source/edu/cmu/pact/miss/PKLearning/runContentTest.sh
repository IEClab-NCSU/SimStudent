#!/bin/bash
echo ${OS}
CommJar="c:/SimStudent/pact-cvs-tree/AuthoringTools/java/lib/CommWidgets.jar"
if [ "${OS}" = "Windows_NT" ]; then
    CPATH="${CommJar};..;."
    VmOption="-cp ${CPATH} -Xmx512m"
fi
if [ "${OS}" != "Windows_NT" ]; then
    CPATH="${CommJar}:..:."
    VmOption="-cp ${CPATH}"
fi

VmOption="${VmOption} -DssFoilBase=../../../../../../FOIL6"
	
operatorFile="operators.txt"
featurePredicateFile="feature-predicates.txt"
wmeTypeFile="wmeTypes.clp"
wmeStructureFile="wmeStructure.txt"
wmeInitFile="init.wme"

instructionsFile="test_items/[01-04]_identifying_terms.txt"

echo java ${VmOption} edu.cmu.pact.miss.PKLearning.PKLearner ${operatorFile} ${featurePredicateFile} ${wmeTypeFile} ${wmeStructureFile} ${wmeInitFile} ${instructionsFile}
java ${VmOption} edu.cmu.pact.miss.PKLearning.PKLearner ${operatorFile} ${featurePredicateFile} ${wmeTypeFile} ${wmeStructureFile} ${wmeInitFile} ${instructionsFile}
