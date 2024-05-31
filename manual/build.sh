#!/bin/bash
# file: bash.sh
set -e

echo "removing build/"
rm -rf "build/"

# hardcode mac for now
PLATFORM="macosx-arm64"
OUTPUT_DIR="build/$PLATFORM"

mkdir -p "$OUTPUT_DIR"

g++ -dynamiclib -o "$OUTPUT_DIR/libmyhello.dylib" ./src/main/cpp/myhello.cpp
