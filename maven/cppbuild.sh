#!/bin/bash
set -e
# file: cppbuild.sh

# Get the absolute path of the directory containing the bash script
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# note: platform is set by the gradle plugin
# echo "platform: $PLATFORM"

# hardcode mac for now
PLATFORM="macosx-arm64"
OUTPUT_DIR="$SCRIPT_DIR/cppbuild/$PLATFORM/lib"

# Create the output directory using the absolute path
mkdir -p "$OUTPUT_DIR"

# Compile the C++ code into a dynamic library using the absolute path
g++ -dynamiclib -o "$OUTPUT_DIR/libmyhello.dylib" "$SCRIPT_DIR/src/main/cpp/myhello.cpp"

# # Compile the C++ code into a dynamic library using the absolute path
# g++ -dynamiclib -o "$SCRIPT_DIR/src/main/cpp/libmyhello.dylib" "$SCRIPT_DIR/src/main/cpp/myhello.cpp"
