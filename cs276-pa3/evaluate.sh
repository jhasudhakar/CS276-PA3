#!/bin/bash
if [ $# -lt 2 ]; then
    echo "Usage: ./evaluate.sh <ranking_output> <relevance_oracle>"
    exit 1
fi

java -Xmx1024m -cp out/production/cs276-pa3 edu.stanford.cs276.NDCG $1 $2
