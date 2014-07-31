#!/bin/bash

export CLUSTER_IP=127.0.0.1
export CLUSTER_PORT=$1

if [ "$2" != "" ]; then
    a=akka.tcp://cluster@127.0.0.1:$2
fi
if [ "$3" != "" ]; then
    b=akka.tcp://cluster@127.0.0.1:$3
fi

sbt "run $a $b"
