#See TSLTReadMe.txt for more information.. 
#If you are not Borg, it probably means the TSLT won't work for you
#unless you make some edits to this file. Suggestions can be found in
#TSLTReadMe.txt
#!/bin/bash
USAGE="Please enter all the log file names, followed by the amount of simulated students per log file, followed by the amount of sequential runs"
echo USAGE
echo "A total of numlogfiles*numstudents*numruns directories are created by this bash script. Each time it runs it deletes all the directories recursively. Each directory is of the form logLSR. "L" represents the nth log file. "S" represents the nth student. "R" represents the nth run. So directory log234 holds the information for 3nd log file being run with the 4th student for the 5th time. For simplicity this program currently only accepts up to 10 log files (0 through 9), 10 students, and 10 runs. This is a 1000 runs in total or in other words 100 at a time executed 10 times in a row."
#D="C:/Users/Borg/Desktop/pact-cvs-tree/AuthoringTools/java/test/edu/cmu/pact/TutoringService/preProcessedInputFolder"
D=$pwd
#setting classpath seperator 
S=:
set -x
rm -r preProcessedInputFolder
#typeset -a logs;
#typeset -a parameters;

params=($@);
num_students=${params[$(($#-2))]}
num_runs=${params[$(($#-1))]}
num_logs=$(($#-2))
port=1516;
mkdir preProcessedInputFolder;
cp TSLTtemplate.html preProcessedInputFolder/TSLTtemplate.html
cp TSLT.swf preProcessedInputFolder/TSLT.swf;
for((i=0; i <$num_logs; i++))
	do
		cp ${params[$i]} preProcessedInputFolder/${params[$i]}; 
	done;
cd preProcessedInputFolder;	
curr_dir=$(pwd);
java  -cp "../../../../../../classes$S../../../../../../lib/CommWidgets.jar" edu.cmu.pact.HTTPServer $port $curr_dir & serverpid=$!;
chmod +r+w+x *.*;
for((i=0; i <$num_logs; i++))
	do
	java -cp "../../../../../../testclasses$S../../../../../../classes$S../../../../../../lib/CommWidgets.jar" edu.cmu.pact.TutoringService.TSLTPreProcessor ${params[$i]} ${params[$i]}.comp ${params[$i]}.input;
	done;
chmod +r+w+x *.*;
for((i=0; i<$num_runs; i++))
	do
	#for ((j=0; j<$(($#-2)); j++))
	for ((j=0; j<$num_logs; j++))
		do
		#logs[$j]=${params[$j]}
		for((k=0; k<$num_students; k++))
			do			
			echo ${params[$j]}
			echo log$j$k$i/${params[$j]};
			mkdir log$j$k$i
			cp TSLTtemplate.html log$j$k$i/TSLTtemplate.html
			cp ${params[$j]}.comp log$j$k$i/${params[$j]}.comp;
			cp TSLT.swf log$j$k$i/TSLT.swf;
			cd log$j$k$i;
			chmod +r+w+x *.*;
			java -cp "../../../../../../../testclasses$S../../../../../../../lib/CommWidgets.jar" edu.cmu.pact.TutoringService.TSLTPreProcessor ${params[$j]} $j$k$i $port $port;
			cd ..;
			done;
		done;
	done;
java  -cp "../../../../../../testclasses$S../../../../../../lib/CommWidgets.jar" edu.cmu.pact.TutoringService.TSLTListener & listenerpid=$!;
#curl -G 128.2.176.162:8080/test.sh
#sleep 30;

#D="file:$(pwd)";
#Depending on which environment you're running in and on what web browser you are using
#You might need to hardcode the variable D

for((i=0; i<$num_runs; i++))
	do
	sleep 2;
	opera -nowin -nosession & browserpid=$!
	sleep 2;	
	for ((j=0; j<$(($#-2)); j++))
		do
		for((k=0; k<$num_students; k++))
			do
			opera "$curr_dir/log$j$k$i/TSLTtemplate.html" & echo "running html for log$j$k$i" & sleep 0.3	
			done;
		done;
	sleep 40;	
	kill -9 ${browserpid};	
	done;
kill -9 ${listenerpid};
sleep 2;
kill -9 ${serverpid};
for((i=0; i<$num_runs; i++))
	do
	for ((j=0; j<$(($#-2)); j++))
		do
		for((k=0; k<$num_students; k++))
			do
				echo Comparing ${params[$j]}.comp to ${params[$j]}.test;
				java -cp "../../../../../../testclasses$S../../../../../../lib/CommWidgets.jar" edu.cmu.pact.TutoringService.TSLTCorrectnessChecker ${params[$j]}.comp ${params[$j]}$j$k$i.test;
				sleep 1;
			done;
		done;
	done;
echo "DONE DONE DONE"
