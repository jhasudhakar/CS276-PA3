#!/bin/bash

if [ $# -lt 1 ]; then
    echo "Usage: ./run <config.ser>"
    exit 1
fi

java -Xmx1024m -cp out/production/cs276-pa3 edu.stanford.cs276.BM25Tuner\$ResultViewer $1
