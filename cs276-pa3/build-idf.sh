#!/bin/bash

if [ $# -lt 1 ]; then
    echo "Usage: ./build-idf.sh <data_ir>"
    exit 1
fi

java -Xmx1024m -cp out/production/cs276-pa3 edu.stanford.cs276.LoadHandler $@
