#!/bin/bash
if [ $# -lt 2 ]; then
    echo "Usage: ./run <train|dev> <scorer>"
    exit 1
fi

data="$1"
scorer="$2"

./rank.sh data/pa3.signal.$data $scorer | ./evaluate.sh data/pa3.rel.$data -
