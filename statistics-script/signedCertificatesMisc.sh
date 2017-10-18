#!/bin/bash

### To execute this script use it as follows ./signedCertificatesMisc.sh <start-date> <end-date>
###
### Where:
### basefarm-username is the username which have VPN access to the misc-server
### start-date and end-date are the dates you are interested in (inclusive) in the format YYYY-mm-dd
###
### Prerequisites:
### VPN access to Basefarm activated

### We have to use eval here as {}-expansion is performed before variable-evaluation. This is not ideal.

if [ $# -lt 2 ]; then
    echo "Wrong syntax. Correct syntax is $0 <start-date> <end-date>"
    exit 1
fi

STARTDATE=$1
ENDDATE=$2

DATES=$STARTDATE
d=; n=0; until [ "$d" = "$ENDDATE" ]; do ((n++)); d=$(date -d "$STARTDATE + $n days" +%Y-%m-%d); DATES+=" $d"; done

DATES=`sed 's/-/\//g' <<< $DATES`
DATES=`sed 's/ /,/g' <<< $DATES`
DATES="{$DATES}"


printf "Totalt\n"
eval zgrep "CERTIFICATE_REGISTERED" /mnt/tomcat_logs/ine-pib-app0{1,2}/minaintyg/archive/$DATES/monitoring.* | cut -d"'" -f6 | sed 's/-.*//' | sort | uniq -c | sed -r 's/^\s*([0-9]+)\s(.*)$/\1,\2/g'

printf "\n\nfk7263\n"
eval zgrep "CERTIFICATE_REGISTERED" "/mnt/tomcat_logs/ine-pib-app0{1,2}/minaintyg/archive/$DATES/monitoring.*" | grep "fk7263" | cut -d"'" -f6 | sed 's/-.*//' | sort | uniq -c | sed -r 's/^\s*([0-9]+)\s(.*)$/\1,\2/g'
