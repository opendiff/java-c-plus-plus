// file: build.gradle.kts
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

    // alternatively: use local jars
    // use local jars from: https://github.com/bytedeco/javacpp/releases/tag/1.5.10
    //  implementation(files("$projectDir/javacpp-platform-1.5.10-bin/javacpp.jar"))
    //  implementation(files("$projectDir/javacpp-platform-1.5.10-bin/javacpp-platform.jar"))
    //  implementation(files("$projectDir/javacpp-platform-1.5.10-bin/javacpp-macosx-arm64.jar"))
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

// Extension method to configure all tasks of type org.bytedeco.gradle.javacpp.BuildTask
fun TaskContainer.configureAllJavacppBuildTasks(action: org.bytedeco.gradle.javacpp.BuildTask.() -> Unit) {
    withType<org.bytedeco.gradle.javacpp.BuildTask>().configureEach(action)
}

// Extension methods for specific named tasks
fun TaskContainer.configureJavacppBuildParser(action: org.bytedeco.gradle.javacpp.BuildTask.() -> Unit) {
    named<org.bytedeco.gradle.javacpp.BuildTask>("javacppBuildParser").configure(action)
}

fun TaskContainer.configureJavacppBuildCommand(action: org.bytedeco.gradle.javacpp.BuildTask.() -> Unit) {
    named<org.bytedeco.gradle.javacpp.BuildTask>("javacppBuildCommand").configure(action)
}

fun TaskContainer.configureJavacppBuildCompiler(action: org.bytedeco.gradle.javacpp.BuildTask.() -> Unit) {
    named<org.bytedeco.gradle.javacpp.BuildTask>("javacppBuildCompiler").configure(action)
}

// Use the extension method to configure all tasks of type org.bytedeco.gradle.javacpp.BuildTask
tasks.configureAllJavacppBuildTasks {
    includePath = arrayOf("${layout.buildDirectory}/$javacppPlatform/include")
    linkPath = arrayOf("${layout.buildDirectory}/$javacppPlatform/lib")
}

// Use the extension methods to configure specific named tasks
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
