#!/usr/bin/env sh
# ./rank.sh <queryDocTrainData path> taskType
java -Xmx1024m -cp bin/production/cs276-pa3 edu.stanford.cs276.Rank $1 $2
