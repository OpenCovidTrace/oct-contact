#!/usr/bin/env bash

echo "--- Stopping staging server..."

./stop_staging.sh

echo "--- Fetching latest code from GitLab..."

git pull

echo "--- Performing clean build..."

sbt clean stage

echo "--- Starting staging server..."

./start_staging.sh

echo "--- Done!"
