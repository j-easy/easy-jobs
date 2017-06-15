#!/bin/bash
set -e

if [ "$#" -ne 1 ]; then
    echo "Please provide the path to Easy Jobs configuration file"
    exit 1;
fi

CONFIG_FILE=$1
WORKING_DIR=`pwd`
ROOT_DIR=`dirname ${WORKING_DIR}`
DB_TYPE=$(cat ${CONFIG_FILE} | grep 'database.type' | cut -d : -f 2 | tr '"' ' ' | tr ',' ' ' | sed 's/^ *//;s/ *$//')

java -cp "${ROOT_DIR}/jobs/*:${ROOT_DIR}/lib/*:${ROOT_DIR}/drivers/${DB_TYPE}/*" \
 -Djava.util.logging.config.file=${ROOT_DIR}/conf/logging.properties \
 -Deasy.jobs.configuration.path=${CONFIG_FILE} \
 org.jeasy.jobs.JobServer &
