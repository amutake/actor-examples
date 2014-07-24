#!/bin/bash

export CLUSTER_IP=127.0.0.1

if [ "$1" != "" ]; then
    a=akka.tcp://cluster@127.0.0.1:$1
fi
if [ "$2" != "" ]; then
    b=akka.tcp://cluster@127.0.0.1:$2
fi
if [ "$3" != "" ]; then
    c=akka.tcp://cluster@127.0.0.1:$3
fi

sbt "run $a $b $c"
