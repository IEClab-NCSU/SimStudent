#!/bin/bash

function usage() {
	msg=""
	if test -n "$1"; then
		msg="$1 "
    fi
    echo "${msg}Usage: $0 [-u user] [-v] sinceDate [file...]"
	echo "where--"
	echo "  -u user   means limit to actions by this CVS user;"
	echo "  -v        means show diffs in addition to log entries;"
	echo "  sinceDate earliest changes to show."
    echo "E.g.: $0 2009/05/25"
    exit 1
}

if test -z "$1"; then
	usage "Must have sinceDate argument."
fi

while test $# -gt 0 -a "$1" != ${1#-}; do
    if test "$1" == "-u"; then
		shift 1
		cvsUser="$1"
    elif test "$1" == "-v"; then
		verbose="$1"
    fi
    shift 1
done

if test -z "$1"; then
	usage "Must have sinceDate argument."
fi

sinceDate="$1"
shift 1
files="$*"
if test -z $cvsUser; then
	cvsUser="-a"
else
	cvsUser="-u $cvsUser"
fi

cvs history -c $cvsUser -D $sinceDate $files | \
sort -k 2 -k 3 -k 8 -k 7 | \
awk -v showDiff=$verbose '
  $7~/.*\.java/ && $8~/AuthoringTools.java/ {
      split($6,r,"."); d=substr($8,21);
      printf("cvs log -N -S -r%d.%d %s/%s\n", r[1],r[2],d,$7 );
      if (showDiff != "") printf("cvs diff -c -r%d.%d -r%d.%d %s/%s\n", r[1],r[2]-1,r[1],r[2],d,$7);
    }
' |\
bash
