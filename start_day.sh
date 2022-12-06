#!/usr/bin/env bash
DAY=$1
FILE="src/day${DAY}.kt"

if [ ! -f $FILE ]; then
  cp src/day00.kt $FILE
  sed -i "s/day00/day${DAY}/g" $FILE
  echo "Created $FILE"
fi
ARGS="-d $DAY"
INPUT="src/day${DAY}.txt"
if [ ! -f $INPUT ]; then
  ARGS="$ARGS -i $INPUT"
fi
PUZZLE="src/puzzle${DAY}.md"
if [ -f $PUZZLE ]; then
  rm -f $PUZZLE
fi
ARGS="$ARGS -p $PUZZLE"
if [ -f input ]; then
  rm -f input
fi
aoc download $ARGS
echo "Downloaded $DAY to $INPUT and $PUZZLE"
