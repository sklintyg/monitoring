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
elif [[ ! ( ( ${FILE} == "logstash-rehabstod.conf" ) || ( ${FILE} == "logstash-minaintyg.conf" ) || ( ${FILE} == "logstash-error-log.conf" ) || ( ${FILE} == "logstash-webcert.conf" ) || ( ${FILE} == "logstash-intygstjanst.conf" ) || ( ${FILE} == "logstash-privatlakarportal.conf" ) || ( ${FILE} == "logstash-statistik.conf" ) ) ]]; then
    echo "File (${FILE}) need to be either logstash-minaintyg.conf, logstash-error-log.conf, logstash-webcert.conf, logstash-intygstjanst.conf, logstash-privatlakarportal.conf, logstash-rehabstod.conf or logstash-statistik.conf"
    exit -1
fi

if [ ! -d ${PATH_TO_PATTERNS} ]; then
    echo "Path to patterns directory (${PATH_TO_PATTERNS}) does not exist.
Maybe 'mkdir -p ${PATH_TO_PATTERNS}' would solve it."
    exit -1
fi

sed -i -e "s#\$PATTERNS_DIR#${PATH_TO_PATTERNS}#g" ${FILE}
