#!/bin/bash
set -e

CONFIG_FILE=$1
WORKING_DIR=`pwd`
ROOT_DIR=`dirname ${WORKING_DIR}`
DB_TYPE=H2 # todo extract db type from config file

java -cp "${ROOT_DIR}/jobs/*:${ROOT_DIR}/lib/*:${ROOT_DIR}/drivers/${DB_TYPE}/*" \
 -Djava.util.logging.config.file=${ROOT_DIR}/conf/logging.properties \
 org.jeasy.jobs.JobServer
