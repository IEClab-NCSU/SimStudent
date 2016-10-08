#!bash

logFile="$1"
if test -z "$logFile" -o "$1" == "--help"; then
    printf "Usage:  %s logFile\n" $(basename $0)
	exit 2
fi
txtFile="${logFile%.log}.txt"

java -cp lib/CommWidgets.jar edu.cmu.oli.log.tools.DiskFileSplitter "$logFile" > "$txtFile"

awk '
    BEGIN                   {printf("%s\t%s\t%s\t%s\n", "user_guid", "session_id", "date_time", "timezone")}
    $0~/<log_session_start/ {s=index($0, "user_guid=\""); R=substr($0,s+11); e=index(R,"\""); u=substr(R,1,e-1);
                             s=index($0, "session_id=\""); R=substr($0,s+12); e=index(R,"\""); ss=substr(R,1,e-1);
                             s=index($0, "date_time=\""); R=substr($0,s+11); e=index(R,"\""); d=substr(R,1,e-1);
                             s=index($0, "timezone=\""); R=substr($0,s+10); e=index(R,"\""); z=substr(R,1,e-1);
                             printf("%s\t%s\t%s\t%s\n",u,ss,d,z)}
' "$txtFile"
