#!/bin/bash

if [ $# -lt 2 ]; then
    echo "Usage: ./run <bm25|cosine|sw> <train|dev>"
    exit 1
fi

scorer="$1"
data="$2"

java -Xmx1024m -cp out/production/cs276-pa3 edu.stanford.cs276.tune.RandomTuner $scorer data/pa3.signal.$data data/pa3.rel.$data
