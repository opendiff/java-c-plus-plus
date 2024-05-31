# JavaCPP Maven Hello World Guide

This guide will walk you through creating a simple "Hello World" application using JavaCPP and Maven. The application will call a native C++ function from Java.

## Prerequisites

- Java Development Kit (JDK) 8 or higher
- Maven
- g++ (GNU Compiler Collection)
- macOS

## Step-by-Step Guide

### 1. Create the `pom.xml` File

Create a `pom.xml` file in the root directory with the following content:

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd">
  <modelVersion>4.0.0</modelVersion>
  
  <groupId>org.example</groupId>
  <artifactId>artifact</artifactId>
  <packaging>jar</packaging>
  <version>1.0-SNAPSHOT</version>
  
  <properties>
    <maven.compiler.source>1.8</maven.compiler.source>
    <maven.compiler.target>1.8</maven.compiler.target>

    <!-- https://github.com/bytedeco/javacpp-presets/blob/master/pom.xml -->
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <javacpp.cppbuild.skip>false</javacpp.cppbuild.skip> <!-- To skip execution of cppbuild.sh: -Djavacpp.cppbuild.skip=true -->
    <javacpp.parser.skip>false</javacpp.parser.skip>     <!-- To skip header file parsing phase: -Djavacpp.parser.skip=true  -->
    <javacpp.compiler.skip>false</javacpp.compiler.skip> <!-- To skip native compilation phase: -Djavacpp.compiler.skip=true -->
    <javacpp.moduleId>${project.artifactId}</javacpp.moduleId>
    <javacpp.packageName>${project.artifactId}</javacpp.packageName>
    <javacpp.platform.nativeOutputPath>org/example/${javacpp.packageName}/${javacpp.platform}${javacpp.platform.extension}</javacpp.platform.nativeOutputPath>
    <javacpp.platform.root></javacpp.platform.root>
    <javacpp.platform.compiler></javacpp.platform.compiler>
    <javacpp.platform.extension></javacpp.platform.extension>
    <javacpp.platform.properties>${javacpp.platform}</javacpp.platform.properties>
    <javacpp.platform.android-arm>android-arm${javacpp.platform.extension}</javacpp.platform.android-arm>
    <javacpp.platform.android-arm64>android-arm64${javacpp.platform.extension}</javacpp.platform.android-arm64>
    <javacpp.platform.android-x86>android-x86${javacpp.platform.extension}</javacpp.platform.android-x86>
    <javacpp.platform.android-x86_64>android-x86_64${javacpp.platform.extension}</javacpp.platform.android-x86_64>
    <javacpp.platform.ios-arm>ios-arm${javacpp.platform.extension}</javacpp.platform.ios-arm>
    <javacpp.platform.ios-arm64>ios-arm64${javacpp.platform.extension}</javacpp.platform.ios-arm64>
    <javacpp.platform.ios-x86>ios-x86${javacpp.platform.extension}</javacpp.platform.ios-x86>
    <javacpp.platform.ios-x86_64>ios-x86_64${javacpp.platform.extension}</javacpp.platform.ios-x86_64>
    <javacpp.platform.linux-armhf>linux-armhf${javacpp.platform.extension}</javacpp.platform.linux-armhf>
    <javacpp.platform.linux-arm64>linux-arm64${javacpp.platform.extension}</javacpp.platform.linux-arm64>
    <javacpp.platform.linux-ppc64le>linux-ppc64le${javacpp.platform.extension}</javacpp.platform.linux-ppc64le>
    <javacpp.platform.linux-x86>linux-x86${javacpp.platform.extension}</javacpp.platform.linux-x86>
    <javacpp.platform.linux-x86_64>linux-x86_64${javacpp.platform.extension}</javacpp.platform.linux-x86_64>
    <javacpp.platform.macosx-arm64>macosx-arm64${javacpp.platform.extension}</javacpp.platform.macosx-arm64>
    <javacpp.platform.macosx-x86_64>macosx-x86_64${javacpp.platform.extension}</javacpp.platform.macosx-x86_64>
    <javacpp.platform.windows-x86>windows-x86${javacpp.platform.extension}</javacpp.platform.windows-x86>
    <javacpp.platform.windows-x86_64>windows-x86_64${javacpp.platform.extension}</javacpp.platform.windows-x86_64>
  </properties>

  <dependencies>
    <dependency>
      <groupId>org.bytedeco</groupId>
      <artifactId>javacpp</artifactId>
      <version>1.5.10</version>
    </dependency>
  </dependencies>

  <build>
    <finalName>${project.artifactId}</finalName>

      <plugins>
          <plugin>
            <artifactId>maven-resources-plugin</artifactId>
            <version>3.3.1</version>
            <executions>
              <execution>
                <id>javacpp-parser</id>
                <phase>generate-sources</phase>
                <goals>
                  <goal>resources</goal>
                </goals>
              </execution>
            </executions>
          </plugin>
          <plugin>
              <groupId>org.apache.maven.plugins</groupId>
              <artifactId>maven-compiler-plugin</artifactId>
              <version>3.12.1</version>
              <configuration>
                  <source>1.8</source>
                  <target>1.8</target>
              </configuration>
              <executions>
                <execution>
                  <id>default-compile</id>
                </execution>
                <execution>
                  <id>javacpp-parser</id>
                  <phase>generate-sources</phase>
                  <goals>
                    <goal>compile</goal>
                  </goals>
                  <configuration>
                    <includes>
                      <include>org/example/presets/*.java</include>
                    </includes>
                  </configuration>
                </execution>
              </executions>
          </plugin>
          <plugin>
            <groupId>org.bytedeco</groupId>
            <artifactId>javacpp</artifactId>
            <version>1.5.10</version>
            <configuration>
              <properties>${javacpp.platform.properties}</properties>
              <propertyKeysAndValues>
                <property>
                  <name>platform.root</name>
                  <value>${javacpp.platform.root}</value>
                </property>
                <property>
                  <name>platform.compiler</name>
                  <value>${javacpp.platform.compiler}</value>
                </property>
                <property>
                  <name>platform.extension</name>
                  <value>${javacpp.platform.extension}</value>
                </property>
              </propertyKeysAndValues>
              <classPath>${project.build.outputDirectory}</classPath>
              <includePaths>
                <includePath>${basedir}/cppbuild/${javacpp.platform}${javacpp.platform.extension}/include/</includePath>
                <includePath>${basedir}/target/classes/org/bytedeco/${javacpp.packageName}/include/</includePath>
                <includePath>${basedir}/src/main/cpp/</includePath> <!-- added -->
              </includePaths>
              <linkPaths>
                <linkPath>${basedir}/cppbuild/${javacpp.platform}${javacpp.platform.extension}/lib/</linkPath>
              </linkPaths>
              <preloadPath>${basedir}/cppbuild/${javacpp.platform}${javacpp.platform.extension}/bin/</preloadPath>
              <resourcePaths>
                <resourcePath>${basedir}/cppbuild/${javacpp.platform}${javacpp.platform.extension}/</resourcePath>
                <resourcePath>${basedir}/target/classes/org/bytedeco/${javacpp.packageName}/</resourcePath>
              </resourcePaths>
              <executablePaths>
                <executablePath>${basedir}/cppbuild/${javacpp.platform}${javacpp.platform.extension}/bin/</executablePath>
              </executablePaths>
              <buildResources>
                <buildResource>/${javacpp.platform.library.path}/</buildResource>
                <buildResource>/org/bytedeco/javacpp/${javacpp.platform}${javacpp.platform.extension}/</buildResource>
                <buildResource>/org/bytedeco/javacpp/${javacpp.platform}/</buildResource>
              </buildResources>
              <includeResources>
                <includeResource>/${javacpp.platform.library.path}/include/</includeResource>
                <includeResource>/org/bytedeco/javacpp/include/</includeResource>
                <includeResource>/org/bytedeco/javacpp/${javacpp.platform}${javacpp.platform.extension}/include/</includeResource>
                <includeResource>/org/bytedeco/javacpp/${javacpp.platform}/include/</includeResource>
              </includeResources>
              <linkResources>
                <linkResource>/${javacpp.platform.library.path}/</linkResource>
                <linkResource>/${javacpp.platform.library.path}/lib/</linkResource>
                <linkResource>/org/bytedeco/javacpp/${javacpp.platform}${javacpp.platform.extension}/</linkResource>
                <linkResource>/org/bytedeco/javacpp/${javacpp.platform}${javacpp.platform.extension}/lib/</linkResource>
                <linkResource>/org/bytedeco/javacpp/${javacpp.platform}/</linkResource>
                <linkResource>/org/bytedeco/javacpp/${javacpp.platform}/lib/</linkResource>
              </linkResources>
            </configuration>
            <executions>
              <execution>
                <id>javacpp-validate</id>
                <phase>validate</phase>
                <goals>
                  <goal>build</goal>
                </goals>
                <configuration>
                  <targetDirectories>
                    <targetDirectory>${project.basedir}/src/gen/java</targetDirectory>
                    <targetDirectory>${project.basedir}/cppbuild/${javacpp.platform}${javacpp.platform.extension}/java/</targetDirectory>
                  </targetDirectories>
                </configuration>
              </execution>
              <execution>
                <id>javacpp-cppbuild-install</id>
                <phase>initialize</phase>
                <goals>
                  <goal>build</goal>
                </goals>
                <configuration>
                  <skip>${javacpp.cppbuild.skip}</skip>
                  <buildCommand>
                    <program>bash</program>
                    <argument>${project.basedir}/cppbuild.sh</argument>
                    <argument>install</argument>
                    <argument>${javacpp.moduleId}</argument>
                    <argument>-platform=${javacpp.platform}</argument>
                    <argument>-extension=${javacpp.platform.extension}</argument>
                  </buildCommand>
                  <environmentVariables>
                    <ANDROID_NDK>${javacpp.platform.root}</ANDROID_NDK>
                  </environmentVariables>
                  <workingDirectory>${project.basedir}/..</workingDirectory>
                </configuration>
              </execution>
              <execution>
                <id>javacpp-cppbuild-clean</id>
                <phase>clean</phase>
                <goals>
                  <goal>build</goal>
                </goals>
                <configuration>
                  <skip>${javacpp.cppbuild.skip}</skip>
                  <buildCommand>
                    <program>bash</program>
                    <argument>${project.basedir}/cppbuild.sh</argument>
                    <argument>clean</argument>
                    <argument>${javacpp.moduleId}</argument>
                  </buildCommand>
                  <workingDirectory>${project.basedir}/..</workingDirectory>
                </configuration>
              </execution>
              <execution>
                <id>javacpp-parser</id>
                <phase>generate-sources</phase>
                <goals>
                  <goal>parse</goal>
                </goals>
                <configuration>
                  <skip>${javacpp.parser.skip}</skip>
                  <outputDirectory>${project.basedir}/src/gen/java</outputDirectory>
                  <classOrPackageName>org.example.presets.*</classOrPackageName>
                </configuration>
              </execution>
              <execution>
                <id>javacpp-compiler</id>
                <phase>process-classes</phase>
                <goals>
                  <goal>build</goal>
                </goals>
                <configuration>
                  <outputDirectory>${project.build.directory}/native/${javacpp.platform.nativeOutputPath}</outputDirectory>
                  <skip>${javacpp.compiler.skip}</skip>
                  <classOrPackageName>org.example.**</classOrPackageName>
                  <copyLibs>true</copyLibs>
                  <copyResources>true</copyResources>
                  <configDirectory>${project.build.directory}/native/META-INF/native-image/${javacpp.platform}${javacpp.platform.extension}/</configDirectory>
                </configuration>
              </execution>
            </executions>
          </plugin>
          <plugin>
              <groupId>org.apache.maven.plugins</groupId>
              <artifactId>maven-jar-plugin</artifactId>
              <version>3.3.0</version>
              <configuration>
                  <archive>
                      <manifest>
                          <addClasspath>true</addClasspath>
                          <mainClass>org.example.Main</mainClass>
                      </manifest>
                  </archive>
              </configuration>
              <executions>
                <execution>
                  <id>default-jar</id>
                  <phase>package</phase>
                  <goals>
                    <goal>jar</goal>
                  </goals>
                  <configuration>
                    <archive>
                      <manifestEntries>
                        <Class-Path>javacpp.jar</Class-Path>
                        <Implementation-Title>${project.name}</Implementation-Title>
                        <Implementation-Vendor>Bytedeco</Implementation-Vendor>
                        <Implementation-Version>${project.version}</Implementation-Version>
                        <Specification-Title>${project.name}</Specification-Title>
                        <Specification-Vendor>Bytedeco</Specification-Vendor>
                        <Specification-Version>${project.version}</Specification-Version>
                        <Multi-Release>true</Multi-Release>
                      </manifestEntries>
                    </archive>
                    <includes>
                      <include>org/example/**</include>
                    </includes>
                    <excludes>
                      <exclude>org/example/include/</exclude>
                    </excludes>
                  </configuration>
                </execution>
                <execution>
                  <id>javacpp-${javacpp.platform}${javacpp.platform.extension}</id>
                  <phase>package</phase>
                  <goals>
                    <goal>jar</goal>
                  </goals>
                  <configuration>
                    <classifier>${javacpp.platform}${javacpp.platform.extension}</classifier>
                    <skipIfEmpty>true</skipIfEmpty>
                    <includes>
                      <!-- In case of successive builds for multiple platforms
                           without cleaning, ensures we only include files for
                           this platform. -->
                      <include>${javacpp.platform.nativeOutputPath}/</include>
                      <include>META-INF/native-image/${javacpp.platform}${javacpp.platform.extension}/</include>
                    </includes>
                    <archive>
                      <manifestEntries>
                        <Multi-Release>true</Multi-Release>
                      </manifestEntries>
                    </archive>
                    <classesDirectory>${project.build.directory}/native</classesDirectory>
                    <excludes>
                      <exclude>org/${javacpp.packageName}/windows-*/*.exp</exclude>
                      <exclude>org/${javacpp.packageName}/windows-*/*.lib</exclude>
                      <exclude>org/${javacpp.packageName}/windows-*/*.obj</exclude>
                    </excludes>
                  </configuration>
                </execution>
              </executions>
          </plugin>

          <plugin>
            <groupId>org.moditect</groupId>
            <artifactId>moditect-maven-plugin</artifactId>
            <version>1.2.1</version>
          </plugin>
          <plugin>
            <artifactId>maven-dependency-plugin</artifactId>
            <version>3.6.1</version>
          </plugin>
          <plugin>
            <artifactId>maven-source-plugin</artifactId>
            <version>3.3.1</version>
          </plugin>
          <plugin>
            <artifactId>maven-javadoc-plugin</artifactId>
            <version>3.6.3</version>
          </plugin>
      </plugins>
  </build>
</project>
```

### 2. Create the C++ Header and Source Files

Create the directory structure for the C++ files:

```sh
mkdir -p src/main/cpp
```

Create the `myhello.h` file in `src/main/cpp` with the following content:

```cpp
#ifndef MYHELLO_H
#define MYHELLO_H

void printHelloWorld2();

#endif // MYHELLO_H
```

Create the `myhello.cpp` file in `src/main/cpp` with the following content:

```cpp
#include <iostream>
#include "myhello.h"

void printHelloWorld2() {
    std::cout << "Hello, World!" << std::endl;
}

```

### 3. Create the Java Source Files

Create the directory structure for the Java files:

```sh
mkdir -p src/main/java/org/example/presets
```

Create the `HelloPreset.java` file in `src/main/java/org/example/presets` with the following content:

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

The `hello.java` file will be auto generated in in `src/gen/java/org/example` with the following content:

```java
// Targeted by JavaCPP version 1.5.10: DO NOT EDIT THIS FILE

package org.example;

import java.nio.*;
import org.bytedeco.javacpp.*;
import org.bytedeco.javacpp.annotation.*;

public class hello extends org.example.presets.HelloPreset {
    static { Loader.load(); }

// Parsed from <myhello.h>

// file: src/main/cpp/myhello.h
// #ifndef MYHELLO_H
// #define MYHELLO_H

public static native void printHelloWorld2();

// #endif // MYHELLO_H


}
```

Create the `Main.java` file in `src/main/java/org/example` with the following content:

```java
package org.example;

import static org.example.hello.printHelloWorld2;

public class Main {

    public static void main(String[] args) {
        printHelloWorld2();
    }
}
```

### 5. Create the Build Script

Create the `cppbuild.sh` file in the root directory with the following content:

```sh
#!/bin/bash
set -e

# Get the absolute path of the directory containing the bash script
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

PLATFORM="macosx-arm64"
OUTPUT_DIR="$SCRIPT_DIR/cppbuild/$PLATFORM/lib"

# Create the output directory using the absolute path
mkdir -p "$OUTPUT_DIR"

# Compile the C++ code into a dynamic library using the absolute path
g++ -dynamiclib -o "$OUTPUT_DIR/libmyhello.dylib" "$SCRIPT_DIR/src/main/cpp/myhello.cpp"
```

Make the script executable:

```sh
chmod +x cppbuild.sh
```

### 6. Build the Project

Run the following command to build the project:

```sh
mvn clean package
```

### 7. Run the Application

Run the following command to execute the application:

```sh
java -Djava.library.path=./target/native/org/example/artifact/macosx-arm64/ -cp target/artifact.jar:javacpp-platform-1.5.10-bin/javacpp.jar org.example.Main
```

You should see the output:

```
Hello, World!
```

## Conclusion

You have successfully created a simple "Hello World" application using JavaCPP and Maven. This guide covered the creation of C++ header and source files, Java source files, a build script, and the Maven configuration.

## Project Structure

Here's the structure of the project:

```
.
├── cppbuild
│   └── macosx-arm64
│       └── lib
│           └── libmyhello.dylib
├── cppbuild.sh
├── javacpp-platform-1.5.10-bin
│   ├── LICENSE.txt
│   └── javacpp.jar
├── pom.xml
├── readme.md
├── src
│   ├── gen
│   │   └── java
│   │       └── org
│   │           └── example
│   │               └── hello.java
│   └── main
│       ├── cpp
│       │   ├── myhello.cpp
│       │   └── myhello.h
│       └── java
│           └── org
│               └── example
│                   ├── Main.java
│                   └── presets
│                       └── HelloPreset.java
└── target
    ├── artifact-macosx-arm64.jar
    ├── artifact.jar
    ├── classes
    │   └── org
    │       └── example
    │           ├── Main.class
    │           ├── hello.class
    │           └── presets
    │               └── HelloPreset.class
    ├── generated-sources
    │   └── annotations
    ├── maven-archiver
    │   └── pom.properties
    ├── maven-status
    │   └── maven-compiler-plugin
    │       └── compile
    │           ├── default-compile
    │           │   ├── createdFiles.lst
    │           │   └── inputFiles.lst
    │           └── javacpp-parser
    │               ├── createdFiles.lst
    │               └── inputFiles.lst
    └── native
        ├── META-INF
        │   └── native-image
        │       └── macosx-arm64
        │           ├── jnihello
        │           │   ├── jni-config.json
        │           │   ├── reflect-config.json
        │           │   └── resource-config.json
        │           └── jnijavacpp
        │               ├── jni-config.json
        │               └── reflect-config.json
        └── org
            └── example
                └── artifact
                    └── macosx-arm64
                        ├── libjnihello.dylib
                        └── libmyhello.dylib
```
