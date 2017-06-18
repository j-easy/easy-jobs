#!/bin/bash
set -e

# todo everything should be optional, default db to h2 + don't add -Deasy.jobs.* flags if not supplied
DB_TYPE=$1
DB_CONFIG_FILE=$2
SERVER_CONFIG_FILE=$3
WORKING_DIR=`pwd`
ROOT_DIR=`dirname ${WORKING_DIR}`

java -cp "${ROOT_DIR}/jobs/*:${ROOT_DIR}/lib/*:${ROOT_DIR}/drivers/${DB_TYPE}/*" \
 -Djava.util.logging.config.file=${ROOT_DIR}/conf/logging.properties \
 -Deasy.jobs.server.config.file=${SERVER_CONFIG_FILE} \
 -Deasy.jobs.database.config.file=${DB_CONFIG_FILE} \
 org.jeasy.jobs.server.JobServer &
