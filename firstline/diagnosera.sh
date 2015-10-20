#!/bin/bash

function help {
    echo "Usage: ./diagnosera.sh <logfile> <troublemakers> <date>"
}

if [ "$1" = "-h" ] || [ "$1" = "--help" ]; then
    help
    exit 0
fi

# First we need to be directed to logfile.
if [[ "$#" -lt 2 ]]; then
    echo "Missing path to logfile and/or list of troublemakers"
    help
    exit -1
fi

# Make sure input are files and we can access them
if [[ ! -f "$1" ]]; then
    echo "$1 is not a file"
    exit -1
elif [[ ! -f "$2" ]]; then
    echo "$2 is not a file"
    exit -1
fi

if [[ "$#" -eq 3 ]]; then
    TIME=`date -I --date="${3}"`
else
    TIME=`date -I`
fi

# Read in known troublemakers for TAK
declare -A troublemakers
while read -r hsa name; do
    troublemakers[$hsa]=$name
done < <(cat $2)

declare -A TAK_ERROR
APPLICATION_ERROR=false
FK_ERROR=false
declare -A UNKNOWN

while read line; do
    ## TAKningsfel
    if [[ -n `echo $line | grep "VP004"` ]]; then
        HSA=${line##*receiverId\:}
        TAK_ERROR["$HSA"]=Found
    elif [[ -n `echo $line | grep "APPLICATION_ERROR"` ]]; then
        APPLICATION_ERROR=true
    elif [[ -n `echo $line | grep "Certificate couldn't be sent to recipient"` ]]; then
        FK_ERROR=true
    else
        line=${line#*] - }
        UNKNOWN[$line]=true
    fi
done < <(cat $1 | grep -i "^${TIME}.*JmsConsumer.*\(WARN\|ERROR\)")

# Iterate over TAK_ERROR and print prettified output
for i in "${!TAK_ERROR[@]}"
do
    if [[ ! -z ${troublemakers[$i]} ]]; then
        echo "Hittade potentiellt takningsfel för känt problematisk enhet: ${troublemakers[$i]}"
    else
        echo "Hittade potentiellt takningsfel för enhet: $i"
    fi
done

# Print message detailing why VAS could not accept our notifications
if $APPLICATION_ERROR ; then
    echo "Problem med kommunikationen till VAS. Detta brukar bero på att de får meddelanden i fel ordning efter ett tidigare stopp."
fi

# FK was temporary (hopefully) down
if $APPLICATION_ERROR ; then
    echo "Intyg kunde inte skickas till Försäkringskassan för att de inte svarar på anrop."
fi

# FK was temporary (hopefully) down
if [[ ${#UNKNOWN[*]} -gt 0 ]]; then
    echo ""
    echo "OBSERVERA!"
    echo "--> Det finns okända fel som behöver utredas. Dessa är:"
    for i in "${!UNKNOWN[@]}"
    do
        echo " * $i"
    done
fi

