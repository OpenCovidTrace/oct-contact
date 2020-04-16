#!/usr/bin/env bash

echo "Killing existing process..."
PID=$(cat target/universal/stage/RUNNING_PID)
kill -9 "$PID"
rm -f target/universal/stage/RUNNING_PID
