count=0
for filename in ./prs/*
do
	echo $filename
	cp $filename productionRules.pr
	./runValidation.sh $filename
done;
#val="test2"
#cp productionRules-$val.pr productionRules.pr
#./runValidation.sh $val