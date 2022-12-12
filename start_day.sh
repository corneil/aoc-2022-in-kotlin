#!/usr/bin/env bash
DAY=$1
FOLDER="src/main/day${DAY}"
FILE="$FOLDER/day${DAY}.kt"

mkdir -p "$FOLDER"
if [ ! -f $FILE ]; then
  cp src/main/day00/day00.kt $FILE
  sed -i "s/00/${DAY}/g" $FILE
  echo "Created $FILE"
  echo "currentDay=$DAY" > gradle.properties
fi
ARGS="-d $DAY"
INPUT="src/main/resources/day${DAY}.txt"
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
