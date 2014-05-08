#!/usr/bin/env sh
# ./rank.sh <queryDocTrainData path> taskType
java -Xmx1024m -cp out/production/cs276-pa3 edu.stanford.cs276.Rank $1 $2
