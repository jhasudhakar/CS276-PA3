#!/bin/bash
if [ $# -lt 1 ]; then
    echo "Usage: ./evaluate.sh <relevance_oracle> <ranking_output|->"
    exit 1
fi

java -Xmx1024m -cp out/production/cs276-pa3 edu.stanford.cs276.NDCG $2 $1
