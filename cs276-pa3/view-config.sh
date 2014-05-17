#!/bin/bash

if [ $# -lt 1 ]; then
    echo "Usage: ./run [-n] <config.ser>"
    exit 1
fi

java -Xmx1024m -cp out/production/cs276-pa3 edu.stanford.cs276.tune.RandomTuner\$ResultViewer $@
