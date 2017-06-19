#!/bin/bash
set -e

PID=$(cat process.id | head -1)
KILL -SIGTERM ${PID} # no -SIGKILL since may kill running jobs
rm process.id
