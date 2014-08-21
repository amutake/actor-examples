#!/bin/bash

export CLUSTER_PORT=$1
sbt "run $2"
