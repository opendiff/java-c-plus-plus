# JNI HelloWorld Example

This guide will walk you through creating a simple "Hello, World" application using Java Native Interface (JNI). The application will call a native C++ method from Java, which returns a byte array containing the message "Hello, World from C++!".

## Prerequisites

- Java Development Kit (JDK) installed
- C++ compiler (e.g., g++)
- Basic knowledge of Java and C++
- macOS

## Step-by-Step Instructions

### 1. Create the Java Class

Create a Java class named `HelloWorld.java` with a native method declaration and a static block to load the shared library.

```java
public class HelloWorld {
  // Declare a native method that returns a byte array
  public native byte[] getBytes();

  // Load the shared library
  static {
      System.loadLibrary("hello");
  }

  public static void main(String[] args) {
      HelloWorld hw = new HelloWorld();
      byte[] bytes = hw.getBytes();
      System.out.println(new String(bytes));
  }
}
```

### 2. Compile the Java Class and Generate Header File

Compile the Java class and generate the JNI header file using the `javac` command with the `-h` option.

```sh
javac -h . HelloWorld.java
```

This command will generate `HelloWorld.class` and `HelloWorld.h`.

### 3. Create the C++ Implementation

Create a C++ source file named `HelloWorld.cpp` and implement the native method.

```cpp
#include <jni.h>
#include <cstring>
#include "HelloWorld.h"

JNIEXPORT jbyteArray JNICALL Java_HelloWorld_getBytes(JNIEnv *env, jobject) {
    const char* message = "Hello, World from C++!";
    jsize len = strlen(message);
    jbyteArray byteArray = env->NewByteArray(len);
    env->SetByteArrayRegion(byteArray, 0, len, reinterpret_cast<const jbyte*>(message));
    return byteArray;
}
```

### 4. Compile the Shared Library

Compile the C++ source file into a shared library. On macOS, you can use the following `g++` command:

```sh
g++ -shared -fPIC -o libhello.dylib HelloWorld.cpp -I${JAVA_HOME}/include -I${JAVA_HOME}/include/darwin
```

For Linux, the command would be:

```sh
g++ -shared -fPIC -o libhello.so HelloWorld.cpp -I${JAVA_HOME}/include -I${JAVA_HOME}/include/linux
```

For Windows, you would use a different command and file extension (e.g., `.dll`).

### 5. Run the Java Application

Run the Java application to see the output from the native method.

```sh
java HelloWorld
```

You should see the following output:

```
Hello, World from C++!
```

## Summary

In this guide, you learned how to:

1. Create a Java class with a native method.
2. Generate a JNI header file.
3. Implement the native method in C++.
4. Compile the C++ code into a shared library.
5. Run the Java application to call the native method.

This simple example demonstrates the basics of using JNI to call native code from Java. You can extend this approach to more complex applications as needed.
