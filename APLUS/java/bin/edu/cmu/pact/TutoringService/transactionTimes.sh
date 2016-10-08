#!/bin/bash

f="$1"
if test -z "$f" -o ! -r "$f"; then
  echo "File \"$f\" not found or not readable."
  exit 2
fi

printf "%6s\t%33s\t%39s\t%10s\t%5s\t%2s\n" "userid" "session_id" "transaction_id" "start_time" "max_elapsed_sec"
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
	BEGIN {i=0}
	$4==sess && $2=="+tsltsp+" && $0~/<MessageType>InterfaceAction<.MessageType>/ {
	    b=index($0, "<transaction_id>")+16; e=index($0, "</transaction_id>"); tx=substr($0, b, e-b);
	    t=((substr($1,2,2)*60)+substr($1,5,2))*60+substr($1,8,2)+0; txTS[tx]=$1; txT[tx]=t; txE[tx]=0-1;
	}
	$4==sess && $2=="+tsltstp+" {
	    b=index($0, "<transaction_id>")+16; e=index($0, "</transaction_id>"); tx=substr($0, b, e-b);
	    t=((substr($1,2,2)*60)+substr($1,5,2))*60+substr($1,8,2)+0; e=t-txT[tx];
	    if (txE[tx]=="" || txE[tx] < e) txE[tx]=e;
	}
        END {
	    for (tx in txTS) { printf("%s\t%s\t%s\t%10s\t%4d\n", uid, sess, tx, txTS[tx], txE[tx]) }
	}
      ' "$f"
      done
)
