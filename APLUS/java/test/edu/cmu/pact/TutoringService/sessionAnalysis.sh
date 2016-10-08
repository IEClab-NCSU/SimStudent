#!/bin/bash

f="$1"
if test -z "$f" -o ! -r "$f"; then
  echo "File \"$f\" not found or not readable."
  exit 2
fi

printf "%6s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\n" LineNo Evt user_id session_id sock_conn first_msg last_msg no_msgs thread_in controller;
typeset -a uid
typeset -a sess
export uid
export sess
awk '
    $0~/<MessageType>SetPreferences<.MessageType>/ {
        b=index($0, "<session_id>")+12; e=index($0, "</session_id>"); sess=substr($0, b, e-b);
        b=index($0, "<user_guid>")+11; e=index($0, "</user_guid>"); uid=substr($0, b, e-b);
        printf("%s\t%s\n", uid, sess)}
' $f | \
sort -u | \
(
    typeset -i i=0; while read u s; do i=$i+1; uid[$i]="$u"; sess[$i]="$s"; done
    typeset -i j=0; while test $((j++)) -lt $i; do
      awk -v uid="${uid[$j]}" -v sess="${sess[$j]}" '
        BEGIN {
            ctrlpkg="edu.cmu.pact.BehaviorRecorder.Controller."; ctrlpkgL=length(ctrlpkg);
        }
        index($0, sess)>0 && $4=="LauncherServer.Session.run()" && $5=="controller" {
            lineno=NR; sc=substr($1,2,length($1)-2);
            ctlr=substr($6,1+ctrlpkgL,length($6)-(ctrlpkgL+1)); ctlrfull=substr($6,1,length($6)-1); thrp=substr($0, index($0, ">Thread-")+8); ts=tt;
        }
        $2=="+sp+" && $4=="Controller" && $5=="=" && $6==ctlrfull && $7~/<edu.cmu.pact.SocketProxy.SocketProxy:/ {
            if (thri != "") printf("%6d\tSoc\t%s\t%s\t%s\t%s\t%s\t%d\t%d\t%s\n", lineno, uid, sess, sc, ts, sd, nmsgs, -thri, ctlr);
            lineno=NR; sc=substr($1,2,length($1)-2); nmsgs=1;
            thri=substr($7, index($7, "Thread-")+6); thriL=length(thri); ts="";
        }
        index($NF, "Thread")+6==index($NF, thri) && index($NF, thri)+thriL-1==length($NF) && ( $4=="readToEom(0):" || $4=="CloseConnect():" ) {
            tt=substr($1,2,length($1)-2); if (ts=="") ts=tt; if (NF<6 || $4=="CloseConnect():") sd=tt; else nmsgs++;
        }
        END {printf("%6d\tEND\t%s\t%s\t%s\t%s\t%s\t%d\t%d\t%s\n", lineno, uid, sess, sc, ts, sd, nmsgs, -thri, ctlr)}
      ' "$f"
      done
)
