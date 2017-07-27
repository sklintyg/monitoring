#!/bin/bash

### To execute this script use it as follows ./signedCertificatesWebcert.sh <basefarm-username> <start-date> <end-date>
###
### Where:
### basefarm-username is the username which have VPN access to the misc-server
### start-date and end-date are the dates you are interested in (inclusive) in the format YYYY-mm-dd
###
### Prerequisites:
### VPN access to Basefarm activated

function show_help() {
    echo "Usage:"
    echo "    ./signedCertificatesWebcert.sh [OPTION]... BASEFARM-USERNAME START-DATE END-DATE"
    echo ""
    echo "OPTIONS:"
    echo "    -- No more arguments after this"
    echo "    -t Only get certificates of specific types (use regex)."
    echo "       PLEASE NOTE: Webcert 5.2 is the first version to use intyg_type in loggpost of signed certificates"
    echo "    -o Only get certificates signed from certain origin (use regex)"
}

# Reset in case getopts has been used previously in the shell.
OPTIND=1

# Default all
origin=''
intyg_type=''

while getopts "ht:o:" opt; do
    case "$opt" in
    h|\?)
        show_help
        exit 0
        ;;
    o)  origin=$OPTARG
        ;;
    t)  intyg_type=$OPTARG
        ;;
    esac
done

shift $((OPTIND-1))

[ "$1" = "--" ] && shift

if [ $# -lt 3 ]; then
    show_help
    exit 1
fi

USERNAME=$1
STARTDATE=$2
ENDDATE=$3

DATES=$STARTDATE
d=; n=0; until [ "$d" = "$ENDDATE" ]; do ((n++)); d=$(date -d "$STARTDATE + $n days" +%Y-%m-%d); DATES+=" $d"; done

DATES=`sed 's/-/\//g' <<< $DATES`
DATES=`sed 's/ /,/g' <<< $DATES`
DATES="{$DATES}"


ssh $USERNAME@ine-pei-misc01.sth.basefarm.net 2>/dev/null << EOF

    printf "antal,läkare,enhet,inloggningssätt\n"
    zgrep INTYG_SIGNED /mnt/tomcat_logs/ine-pib-app0{1,2}/webcert/archive/$DATES/webcert-monitoring* | sed 's/NO /NO_/g' | grep "$origin" | grep "of type '$intyg_type" | cut -d" " -f4,5,6 | sort | uniq -c | sed -r 's/^\s+//' | sed 's/ /,/g'

EOF
