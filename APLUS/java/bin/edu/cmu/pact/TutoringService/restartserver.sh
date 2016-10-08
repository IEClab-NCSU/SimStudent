#!/bin/bash

HOME=/usr0/tutorservice
pidFile="$HOME/.pid"

# Kill current instance by reading PID from $pidFile
killByPid() {
    if test -r "$pidFile"; then
	pid=$(cat "$pidFile")
	rm -f "$pidFile"
	kill $pid
	result=$?
	sleep 2
	if ps -aef | awk '{print $2}' | grep $pid; then
	    echo "Unable to kill tutorservice process pid=" $pid
	    return 1
	else
	    return $result
	fi
    else
	return 1
    fi
}

# Kill current instance by reading PID from `ps` output
killByName() {
    if ps -aef | grep "$HOME" | grep -v grep; then
	pid=$(ps -aef | grep "$HOME" | awk 'index($0,"grep")<1 {print $2}')
	if test -n "$pid"; then
	    kill $pid
	    sleep 2
	    result=$?
	    if ps -aef | grep "$HOME" | grep -v grep; then
		echo "Unable to kill tutorservice process pid=" $pid
		return 1
	    else
		return $result
	    fi
	fi
    fi
}
if ! killByPid; then
    killByName
fi

# LauncherServer script writes pidFile
/usr0/tutorservice/LauncherServer.sh &

