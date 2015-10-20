#!/bin/bash

### Parse input
if [ $# -lt 2 ]; then
    echo "Too few arguments"
    exit 1;
fi

STARTDATE=$1
ENDDATE=$2


### Generate list of indices
CURR="$ENDDATE"
while true; do
    [ "$CURR" \> "$STARTDATE" ] || break
    INDICES="$INDICES,logstash-$(echo $CURR | sed -e 's/-/./g')"
    CURR=$( date +%Y-%m-%d --date "$CURR -1 day" )
done

# Trim first ','
INDICES=${INDICES:1}

### Setup general parameters for queries
URL="http://prod-logstash-box-inera-se.sth.basefarm.net/$INDICES/_search?pretty"
SECTOMS=000
FROM=$(date +"%s" --date "$STARTDATE")
FROM="$FROM$SECTOMS"
TO=$(date +"%s" --date "$ENDDATE")
TO="$TO$SECTOMS"

### Login query
LOGIN_QUERY=`cat login.query`
LOGIN_QUERY=$(echo $LOGIN_QUERY | sed -e "s/FROM_VAR/$FROM/")
LOGIN_QUERY=$(echo $LOGIN_QUERY | sed -e "s/TO_VAR/$TO/")

# Execute query against kibana
curl -XGET $URL -d "$LOGIN_QUERY" 2> /dev/null > login.json

echo "Inloggningar"
python parse_login.py

### Certificates query
SIGNED_QUERY=`cat signed.query`
SIGNED_QUERY=$(echo $SIGNED_QUERY | sed -e "s/FROM_VAR/$FROM/")
SIGNED_QUERY=$(echo $SIGNED_QUERY | sed -e "s/TO_VAR/$TO/")

# Execute query against kibana
curl -XGET $URL -d "$SIGNED_QUERY" 2> /dev/null > signed.json

echo ""
echo "Signeringar"
python parse_sign.py


### QA query
QA_QUERY=`cat qa.query`
QA_QUERY=$(echo $QA_QUERY | sed -e "s/FROM_VAR/$FROM/")
QA_QUERY=$(echo $QA_QUERY | sed -e "s/TO_VAR/$TO/")

# Execute query against kibana
curl -XGET $URL -d "$QA_QUERY" 2> /dev/null > qa.json

echo ""
echo "Fråga och svar"
python parse_qa.py
