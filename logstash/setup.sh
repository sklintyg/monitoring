#!/bin/bash

if [ $# -lt 2 ]; then
    echo "Usage setup.sh <file> <path_to_patterns_dir>"
    exit -1
fi

FILE=$1
PATH_TO_PATTERNS=$2
ALL_FILES="logstash-*.conf"

if [[ ${FILE} == "all" ]]; then
    FILE=${ALL_FILES}
elif [[ ! ( ( ${FILE} == "logstash-mina-intyg.conf" ) || ( ${FILE} == "logstash-webcert-log.conf" ) || ${FILE} == "logstash-webcert-monitoring.conf" ) ]]; then
    echo "File (${FILE}) need to be either logstash-mina-intyg.conf, logstash-webcert-log.conf, or logstash-webcert-monitoring.conf"
    exit -1
fi

if [ ! -d ${PATH_TO_PATTERNS} ]; then
    echo "Path to patterns directory (${PATH_TO_PATTERNS}) does not exist.
Maybe 'mkdir -p ${PATH_TO_PATTERNS}' would solve it."
    exit -1
fi

sed -i -e "s/\$PATTERNS_DIR/${PATH_TO_PATTERNS}/g" ${FILE}
