#!/bin/bash

N=4
typeset -i i=0;
typeset -a PIDs
while test $i -lt $N; do
  i=$(($i+1));
  echo $i;
  /cygdrive/c/Program\ Files/Internet\ Explorer/iexplore.exe http://www.cmu.edu &
  #  ping -n 10 localhost >/dev/null &
  PIDs[$i]=$!
done;
jobs;
sleep 30;
i=0;
while test $i -lt $N; do
  i=$(($i+1));
  echo kill $i ${PIDs[$i]};
  kill ${PIDs[$i]};
done
