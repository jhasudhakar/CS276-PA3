#!/bin/bash

if [ $# -lt 1 ]; then
    echo "Usage: ./run <train|dev>"
    exit 1
fi

data="$1"

java -Xmx1024m -cp out/production/cs276-pa3 edu.stanford.cs276.BM25Tuner data/pa3.signal.$data data/pa3.rel.$data
