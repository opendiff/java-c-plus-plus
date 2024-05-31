#!/bin/bash
# file: build.sh
set -e

# note: platform is set by the gradle plugin
echo "platform: $PLATFORM"

# hardcode mac for now
PLATFORM="macosx-arm64"
OUTPUT_DIR="build/$PLATFORM"

mkdir -p "$OUTPUT_DIR"

g++ -dynamiclib -o "$OUTPUT_DIR/libmyhello.dylib" ./src/main/cpp/myhello.cpp
