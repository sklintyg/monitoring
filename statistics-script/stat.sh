#!/bin/bash

### To execute this script use it as follows ./script.sh <basefarm-username> <start-date> <end-date>
###
### Where:
### basefarm username is the username which have VPN access to the misc-server
### start-date and end-date are the dates you are interested in (inclusive) in the format YYYY-mm-dd
###
### Please note that for VARDGIVARE MISSING to work you need to have the start date in the file catalina.out.

if [ $# -lt 3 ]; then
    echo "Wrong syntax. Correct syntax is ./script <basefarm-username> <start-date> <end-date>"
    exit 1
fi

STARTDATE=$2
ENDDATE=$3

DATES=$STARTDATE
d=; n=0; until [ "$d" = "$ENDDATE" ]; do ((n++)); d=$(date -d "$STARTDATE + $n days" +%Y-%m-%d); DATES+=" $d"; done

DATES=`sed 's/-/\//g' <<< $DATES`
DATES=`sed 's/ /,/g' <<< $DATES`
DATES="{$DATES}"


ssh $1@ine-pei-misc01.sth.basefarm.net 2>/dev/null << EOF

    printf "VARDGIVARE MISSING $STARTDATE TO $ENDDATE\n"
    cat /mnt/tomcat_logs/ine-pib-app03/statistik/catalina.out | sed -n '/^$STARTDATE/,/^$ENDDATE/p' | grep "rdgivare saknas: {" | sed 's/.* - //' | grep "enhet" | sed 's/.*enhet":{"id":"\([^"]*\)".*/\1/' | sort | uniq -c | sort -r | sed 's/^ *//; s/ \+/,/'

    printf "\nVARDGIVARID MISMATCH $STARTDATE TO $ENDDATE\n"
    zgrep "VardgivarId mismatch " /mnt/tomcat_logs/ine-pib-app03/statistik/archive/$DATES/statistics-process.* | cut -d':' -f 5 | sort | uniq -c | sort -r | sed 's/^ *//; s/ \+/,/'

    printf "\nNO CARE GIVER $STARTDATE TO $ENDDATE\n"
    zgrep "no care giver '" /mnt/tomcat_logs/ine-pib-app03/statistik/archive/$DATES/statistics-process.* | grep "jmscontainer" | cut -d"'" -f 2 | sort | uniq -c | sort -r | sed 's/^ *//; s/ \+/,/'

    printf "\nNO PERSON $STARTDATE TO $ENDDATE\n"
    zgrep "No person" /mnt/tomcat_logs/ine-pib-app03/statistik/archive/$DATES/statistics-process.* | grep "jmsContainer" | cut -d"'" -f 2 | sort | uniq -c | sort -r | sed 's/^ *//; s/ \+/,/'

    printf "\n NO UNIT $STARTDATE TO $ENDDATE\n"
    zgrep "No unit" /mnt/tomcat_logs/ine-pib-app03/statistik/archive/$DATES/statistics-process.* | grep "jmsContainer" | cut -d"'" -f 2 | sort | uniq -c | sort -r | sed 's/^ *//; s/ \+/,/'

EOF

