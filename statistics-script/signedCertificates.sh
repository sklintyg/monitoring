#!/bin/bash

### To execute this script use it as follows ./signedCertificates.sh <basefarm-username> <start-date> <end-date>
###
### Where:
### basefarm-username is the username which have VPN access to the misc-server
### start-date and end-date are the dates you are interested in (inclusive) in the format YYYY-mm-dd
###
### Prerequisites:
### VPN access to Basefarm activated

if [ $# -lt 3 ]; then
    echo "Wrong syntax. Correct syntax is ./signedCertificates.sh <basefarm-username> <start-date> <end-date>"
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

    printf "Totalt\n"
    zgrep "CERTIFICATE_REGISTERED" /mnt/tomcat_logs/ine-pib-app0{1,2}/minaintyg/archive/$DATES/monitoring* | cut -d"'" -f6 | sed 's/-.*//' | sort | uniq -c | sed -r 's/^\s*([0-9]+)\s(.*)$/\1,\2/g'

    printf "\n\nfk7263\n"
    zgrep "CERTIFICATE_REGISTERED" /mnt/tomcat_logs/ine-pib-app0{1,2}/minaintyg/archive/$DATES/monitoring* | grep "fk7263" | cut -d"'" -f6 | sed 's/-.*//' | sort | uniq -c | sed -r 's/^\s*([0-9]+)\s(.*)$/\1,\2/g'
EOF

