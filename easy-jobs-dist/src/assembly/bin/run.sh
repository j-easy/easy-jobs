#!/bin/bash
set -e

WORKING_DIR=$(pwd)
ROOT_DIR=$(dirname ${WORKING_DIR})
DB_TYPE="h2"
DB_CONF=${ROOT_DIR}/conf/database.properties
DB_INIT="true"
JOBS_DESCRIPTOR=${ROOT_DIR}/conf/jobs.yml
JOBS_DIRECTORY=${ROOT_DIR}/jobs
SERVER_PORT="8080"
SERVER_WORKER_POOL_SIZE="10"
SERVER_WORKER_NAME_PREFIX="worker-thread-"
SERVER_POLLING_INTERVAL="30"

while [[ "$#" > 1 ]]; do case $1 in
    --database.type) DB_TYPE="$2";;
    --database.conf) DB_CONF="$2";;
    --database.init) DB_INIT="$2";;
    --jobs.descriptor) JOBS_DESCRIPTOR="$2";;
    --jobs.directory) JOBS_DIRECTORY="$2";;
    --server.port) SERVER_PORT="$2";;
    --server.workers.pool.size) SERVER_WORKER_POOL_SIZE="$2";;
    --server.workers.name.prefix) SERVER_WORKER_NAME_PREFIX="$2";;
    --server.polling.interval) SERVER_POLLING_INTERVAL="$2";;
    *) break;;
  esac; shift; shift
done

java -cp "${ROOT_DIR}/lib/*:${ROOT_DIR}/drivers/${DB_TYPE}/*" \
 -Deasy.jobs.database.config.file=${DB_CONF} \
 -Deasy.jobs.database.config.init=${DB_INIT} \
 -Deasy.jobs.server.config.jobs.directory=${JOBS_DIRECTORY} \
 -Deasy.jobs.server.config.jobs.descriptor=${JOBS_DESCRIPTOR} \
 -Deasy.jobs.server.config.workers.pool.size=${SERVER_WORKER_POOL_SIZE} \
 -Deasy.jobs.server.config.workers.name.prefix=${SERVER_WORKER_NAME_PREFIX} \
 -Deasy.jobs.server.config.polling.interval=${SERVER_POLLING_INTERVAL} \
 -Dserver.port=${SERVER_PORT} \
 org.jeasy.jobs.server.JobServer &
