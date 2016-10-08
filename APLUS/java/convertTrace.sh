#!/bin/bash

d="$1"
if test -z "$d"; then
	printf "Usage:\n"
    printf "   %s directory\n" "$(basename $0)"
	printf "where--\n"
	printf "   directory is the target directory (with subdirectories) to process.\n"
    exit 2
fi

flist=$(cvs -q status $d | awk '$1=="Repository" && $2=="revision:" && $4~/\.java,v$/ {b=index($4,d); e=length($4)-(b-1)-2; print substr($4,b,e)}' d=$d)

for f in $flist; do
	printf "\n\n********* %s *********\n\n" $f
	if test 1 -gt $(grep -c 'trace\.' < $f); then
		continue
	fi
	if test 1 -ge $(wc -l < $f); then
		tr \\r \\n < $f >/tmp/junkCR.java
		toEdit="/tmp/junkCR.java"
	else
		toEdit="$f"
	fi
    sed -e 's/^\([ 	]*\)\(trace\.[a-zA-Z][a-zA-Z]*([^),"]*\)"\([^),"]*\)",/\1if (trace.getDebugCode("\3")) \2"\3",/' $toEdit >/tmp/junk.java
	if test 2 -lt $(grep -c '\r\n' < $f); then
		sed -e 's/$/\r/' /tmp/junk.java > /tmp/junkToDiff.java
	else
		cp -p  /tmp/junk.java /tmp/junkToDiff.java
	fi
	awk '/getDebugCode/ {
					while (index($NF, ";") < 1) {
						getline;
					}
					while (1) {
						getline;
						if ($1=="") continue;
						if (index($1, "//") == 1) continue;
						if (index($0, "else") > 0) printf("else in next clause ln. %4d: %s\n", NR, f);
						break
                    }
        }' f="$f" /tmp/junkToDiff.java
    if diff -bu $toEdit /tmp/junkToDiff.java; then
		continue
    fi
	if test "$toEdit" == "/tmp/junkCR.java"; then
		tr \\n \\r < /tmp/junkToDiff.java > /tmp/junkToStore.java
	else
		cp -p /tmp/junkToDiff.java /tmp/junkToStore.java
    fi
	mv -i /tmp/junkToStore.java $f
done

ant jar  # compile to check for errors

printf "Commit? y/N "
read REPLY
if test "$REPLY" == "y"; then
	cvs commit -m "Convert trace.XXX(...) to if (trace.getDebugCode(...)) trace.XXX(...)." $flist
fi
