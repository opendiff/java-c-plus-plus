# README

## Overview

This file highlights the bugs encountered during the process of using javacpp.jar directly on a hello world example.

## Directory Structure

```
tree .
.
├── build
│   ├── macosx-arm64
│   │   ├── libjnihello.dylib
│   │   └── libmyhello.dylib
│   └── org
│       └── example
│           ├── Main.class
│           ├── hello.class
│           └── presets
│               └── HelloPreset.class
├── build.sh
├── javacpp.jar
├── readme.md
└── src
    └── main
        ├── cpp
        │   ├── myhello.cpp
        │   └── myhello.h
        └── java
            └── org
                └── example
                    ├── Main.java
                    ├── hello.java
                    └── presets
                        └── HelloPreset.java
```

## Step-by-Step Guide

### 1. Build the Shared Library

Run the `build.sh` script to create the shared library (`libmyhello.dylib`):

```bash
#!/bin/bash
set -e

echo "removing build/"
rm -rf "build/"

# hardcode mac for now
PLATFORM="macosx-arm64"
OUTPUT_DIR="build/$PLATFORM"

mkdir -p "$OUTPUT_DIR"

g++ -dynamiclib -o "$OUTPUT_DIR/libmyhello.dylib" ./src/main/cpp/myhello.cpp
```

### 2. Compile `HelloPreset.java`

```bash
javac -cp javacpp.jar -d build src/main/java/org/example/presets/HelloPreset.java
```

### 3. Generate `hello.java` and JNI Code

#### Generate `hello.java`

Generate `hello.java` from `org.example.presets.HelloPreset`

```bash
java -cp javacpp.jar:build org.bytedeco.javacpp.tools.Builder -Dplatform.includepath=src/main/cpp  org.example.presets.HelloPreset -d src/main/java
```

#### Generate JNI Code

Compile `hello.java`

```bash
javac -cp javacpp.jar:src/main/java -d build/ src/main/java/org/example/hello.java
```

Generate the JNI bindings for `hello.java`

```bash
java -cp javacpp.jar:build:src/main/java org.bytedeco.javacpp.tools.Builder -Dplatform.includepath=src/main/cpp -Dplatform.linkpath=build/macosx-arm64 org.example.hello -d build/macosx-arm64
```

Verify the binaries contain the printHelloWorld2 method.

> nm -gU build/macosx-arm64/libjnihello.dylib
>
> 0000000000004c74 T _Java_org_example_hello_printHelloWorld2

> nm -gU build/macosx-arm64/libmyhello.dylib
>
> 0000000000003108 T __Z16printHelloWorld2v

### 4. Compile Everything

```bash
javac -cp javacpp.jar:src/main/java -d build/ src/main/java/org/example/*.java
```

### 5. Run the Application

```bash
java -Djava.library.path=./build/macosx-arm64/ -cp build:javacpp.jar org.example.Main
```
