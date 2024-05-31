# JavaCPP Gradle Hello World Example

This README provides a guide to setting up and running a simple "Hello, World!" example using JavaCPP and Gradle.

## Prerequisites

- Java Development Kit (JDK) 17 or higher
- Gradle
- g++ (for compiling C++ code)
- macOS

## Step-by-Step Guide

### 1. Write the C++ Code

Create the following C++ source and header files:

**`src/main/cpp/myhello.cpp`**
```cpp
#include <iostream>
#include "myhello.h"

void printHelloWorld2() {
    std::cout << "Hello, World!" << std::endl;
}

```

**`src/main/cpp/myhello.h`**
```cpp
#ifndef MYHELLO_H
#define MYHELLO_H

void printHelloWorld2();

#endif // MYHELLO_H
```

### 2. Create the Java Preset

Create the following Java preset file:

**`src/main/java/org/example/presets/HelloPreset.java`**
```java
package org.example.presets;

import org.bytedeco.javacpp.*;
import org.bytedeco.javacpp.annotation.*;
import org.bytedeco.javacpp.tools.*;

@Properties(
    value = {
        @Platform(include = "<myhello.h>", link = "myhello")
    },
    target = "org.example.hello"
)
public class HelloPreset implements InfoMapper {
    static {
        Loader.load();
    }

    public void map(InfoMap infoMap) {
    }
}
```

### 3. Create the Main Java Class

Create the following Java main class:

**`src/main/java/org/example/Main.java`**
```java
package org.example;

import static org.example.hello.printHelloWorld2;

public class Main {

    public static void main(String[] args) {
        printHelloWorld2();
    }
}
```

### 4. Create the Build Script

Create the following build script:

**`build.sh`**
```bash
#!/bin/bash
set -e

# note: platform is set by the gradle plugin
echo "platform: $PLATFORM"

# hardcode mac for now
PLATFORM="macosx-arm64"
OUTPUT_DIR="build/$PLATFORM"

mkdir -p "$OUTPUT_DIR"

g++ -dynamiclib -o "$OUTPUT_DIR/libmyhello.dylib" ./src/main/cpp/myhello.cpp
```

### 5. Configure Gradle

Create the following Gradle build script:

**`build.gradle.kts`**
```kotlin
plugins {
    kotlin("jvm") version "1.9.23"
    id("org.bytedeco.gradle-javacpp-build") version "1.5.10"
}


group = "org.example"
version = "1.0-SNAPSHOT"

// see: https://github.com/bytedeco/gradle-javacpp/blob/master/samples/zlib/build.gradle
val javacppPlatform: String by project
val javacppVersion: String = "1.5.10"

repositories {
    mavenCentral()
}

dependencies {
    api("org.bytedeco:javacpp:$javacppVersion")
    javacppPlatform("org.bytedeco:javacpp-platform:$javacppVersion")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "org.example.Main"
    }
}

// copy javacpp-1.5.10.jar dep to ./build/deps
tasks.register("copyDependencies", Copy::class) {
    from(configurations.compileClasspath.get().filter { it.name.startsWith("javacpp") && it.extension == "jar" })
    into(layout.buildDirectory.dir("deps"))
}

// copy deps on assemble
tasks.named("javacppBuildCommand") {
    dependsOn("copyDependencies")
}

fun TaskContainer.configureAllJavacppBuildTasks(action: org.bytedeco.gradle.javacpp.BuildTask.() -> Unit) {
    withType<org.bytedeco.gradle.javacpp.BuildTask>().configureEach(action)
}

fun TaskContainer.configureJavacppBuildParser(action: org.bytedeco.gradle.javacpp.BuildTask.() -> Unit) {
    named<org.bytedeco.gradle.javacpp.BuildTask>("javacppBuildParser").configure(action)
}

fun TaskContainer.configureJavacppBuildCommand(action: org.bytedeco.gradle.javacpp.BuildTask.() -> Unit) {
    named<org.bytedeco.gradle.javacpp.BuildTask>("javacppBuildCommand").configure(action)
}

fun TaskContainer.configureJavacppBuildCompiler(action: org.bytedeco.gradle.javacpp.BuildTask.() -> Unit) {
    named<org.bytedeco.gradle.javacpp.BuildTask>("javacppBuildCompiler").configure(action)
}

tasks.configureAllJavacppBuildTasks {
    includePath = arrayOf("${layout.buildDirectory}/$javacppPlatform/include")
    linkPath = arrayOf("${layout.buildDirectory}/$javacppPlatform/lib")
}

tasks.configureJavacppBuildCommand {
    buildCommand = arrayOf("bash", "build.sh")
    dependsOn("javacppPomProperties", "compileKotlin")
}

tasks.configureJavacppBuildParser {
    classOrPackageNames = arrayOf("org.example.presets.*")
    outputDirectory = file("$projectDir/src/main/java/")
    includePath = arrayOf("$projectDir/src/main/cpp/")
}

tasks.configureJavacppBuildCompiler {
    copyLibs = true
    deleteJniFiles = false
    includePath = arrayOf("$projectDir/src/main/cpp/")
    // build.sh saves the lib here to ./build/macos-arm64/libmyhello.dylib
    linkPath = arrayOf("$projectDir/build/$javacppPlatform")
}
```

### 6. Build and Run the Project

Run the following commands to build and run the project:

```sh
./gradlew clean assemble
java -Djava.library.path=./build/classes/java/main/org/example/macosx-arm64/ -cp build/libs/gradle-1.0-SNAPSHOT.jar:build/deps/javacpp-1.5.10.jar org.example.Main
```

You should see the following output:

```
Hello, World!
```

This completes the setup and execution of a simple "Hello, World!" example using JavaCPP and Gradle.


## Project Structure

Here's an overview of the project structure:

```
.
├── build
│   ├── classes
│   │   └── java
│   │       └── main
│   │           ├── jnijavacpp.cpp
│   │           └── org
│   │               └── example
│   │                   ├── Main.class
│   │                   ├── hello.class
│   │                   ├── jnihello.cpp
│   │                   ├── macosx-arm64
│   │                   │   ├── libjnihello.dylib
│   │                   │   └── libmyhello.dylib
│   │                   └── presets
│   │                       └── HelloPreset.class
│   ├── deps
│   │   └── javacpp-1.5.10.jar
│   ├── generated
│   │   └── sources
│   │       ├── annotationProcessor
│   │       │   └── java
│   │       │       └── main
│   │       └── headers
│   │           └── java
│   │               └── main
│   │                   └── org_example_hello.h
│   ├── libs
│   │   ├── gradle-1.0-SNAPSHOT-macosx-arm64.jar
│   │   └── gradle-1.0-SNAPSHOT.jar
│   ├── macosx-arm64
│   │   └── libmyhello.dylib
│   ├── resources
│   │   └── main
│   │       └── META-INF
│   │           └── maven
│   │               └── org.example
│   │                   └── gradle
│   │                       └── pom.properties
│   └── tmp
│       ├── compileJava
│       │   └── previous-compilation-data.bin
│       ├── jar
│       │   └── MANIFEST.MF
│       ├── javacppCompileJava
│       │   └── previous-compilation-data.bin
│       └── javacppJar
│           └── MANIFEST.MF
├── build.gradle.kts
├── build.sh
├── gradle
│   └── wrapper
│       ├── gradle-wrapper.jar
│       └── gradle-wrapper.properties
├── gradle.iml
├── gradle.properties
├── gradlew
├── gradlew.bat
├── readme.md
├── settings.gradle.kts
└── src
    └── main
        ├── cpp
        │   ├── myhello.cpp
        │   └── myhello.h
        └── java
            └── org
                └── example
                    ├── Main.java
                    ├── hello.java
                    └── presets
                        └── HelloPreset.java
```
