# JavaCPP Hello World Examples

This repository contains various hello world examples using JavaCPP and JNI. Each example is organized into its own directory and showcases different build systems or methods for integrating native code with Java.

## Directory Structure

- [gradle](gradle/readme.md)
  - A JavaCPP example using the Gradle build system. It demonstrates how to set up and use JavaCPP with Gradle to manage dependencies and build the project.
  - The example is based on the official [gradle-javacpp/zlib sample](https://github.com/bytedeco/gradle-javacpp/blob/e4de6606200654777ea9f6af30df10bf835e713d/samples/zlib/build.gradle)

- [maven](maven/readme.md)
  - A JavaCPP example using the Maven build system. It shows how to configure Maven to work with JavaCPP for dependency management and project building.
  - The example is based on the official [javacpp-presets pom.xml](https://github.com/bytedeco/javacpp-presets/blob/f8932a44bc2cd9845d64c685347acb81697a530b/pom.xml)

- [jni_hello_world](jni_hello_world/readme.md)
  - A simple JNI (Java Native Interface) Hello World example. It illustrates the basics of creating and calling native methods from Java using JNI.

- [manual](manual/readme.md)
  - A JavaCPP example that uses the `javacpp.jar` directly without relying on a build system like Gradle or Maven. It demonstrates how to manually set up and use JavaCPP.
  - The example is based on the official [mapping recipes wiki page](https://github.com/bytedeco/javacpp/wiki/Mapping-Recipes#introduction)

## Getting Started

To get started with any of the examples, navigate to the respective directory and follow the instructions provided in the `README.md` file located there.

## Additional Resources

- [JavaCPP Documentation](https://github.com/bytedeco/javacpp)
- [JavaCPP Presets](https://github.com/bytedeco/javacpp-presets)
- [JavaCPP Gradle](https://github.com/bytedeco/gradle-javacpp)
- [JNI Documentation](https://docs.oracle.com/javase/8/docs/technotes/guides/jni/)

Feel free to explore each example to understand how to integrate native code with Java using different tools and methods.
