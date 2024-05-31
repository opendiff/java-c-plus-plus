Note. Manually generating with javacpp jar is buggy. It's best to use javacpp with gradle or maven.

# create: ./manual/build/macosx-arm64/libmyhello.dylib

> ./build.sh

# compile: manual/build/org/example/presets/HelloPreset.class

> javac  -cp ./javacpp-platform-1.5.10-bin/javacpp.jar -d build/ src/main/java/org/example/presets/HelloPreset.java

# generate hello.java & JNI

# hello.java (HelloPreset.java with target="org.example.hello")

> java -cp javacpp-platform-1.5.10-bin/javacpp.jar:build org.bytedeco.javacpp.tools.Builder -Dplatform.includepath=src/main/cpp -Dplatform.linkpath=build/macosx-arm64 org.example.presets.HelloPreset -d src/main/java

# jni gen (HelloPreset.java with DELETED target="org.example.hello")

> java -cp javacpp-platform-1.5.10-bin/javacpp.jar:build org.bytedeco.javacpp.tools.Builder -Dplatform.includepath=src/main/cpp -Dplatform.linkpath=build/macosx-arm64 org.example.presets.HelloPreset -d build/macosx-arm64

# compile everything

```
build/
└── org
    └── example
        ├── Main.class
        ├── hello.class
        └── presets
            └── HelloPreset.class

5 directories, 4 files
```

> javac  -cp ./javacpp-platform-1.5.10-bin/javacpp.jar:src/main/java -d build/ src/main/java/org/example/*.java

# debugging tip

this will print the value of the property.

> java -cp javacpp-platform-1.5.10-bin/javacpp.jar:build org.bytedeco.javacpp.tools.Builder -Dplatform.includepath=src/main/cpp -print platform.includepath 

## bug found

```java
// generates
@Platform(include="NativeLibrary.h",link="MyFunc")
```

```java
// does NOT generate cpp. generates java
@Properties(
    value = {
        @Platform(include = "<myhello.h>", link = "myhello")
    },
    target = "org.example.hello"
)
```

```java
// generates
@Properties(
    value = {
        @Platform(include = "myhello.h", link = "myhello")
    }
)
```

```java
// only generates java
@Properties(target="org.example", value={
  @Platform(include = "myhello.h", link = "myhello")
})
```

the issue with the cpp code not being generated is the presence of the "target = "org.example.hello" annotation.
this does not bother the maven or gradle builds.

manually you can only generate either the java code or the CPP code.

work around is to set annotation to:
```java
 @Properties(target="org.example.hello", value={
   @Platform(include = "myhello.h", link = "myhello")
 })
```

and generate the hello.java code.

then set annotation to:
```java
 @Platform(include = "myhello.h", link = "myhello")
```
and generate the JNI code.

## wip

```java
// hello.java but no cpp
 @Properties(target="org.example.hello", value={
   @Platform(include = "myhello.h", link = "myhello")
 })
L1088 Builder.java

// no hello.java. cpp gen
@Platform(include = "myhello.h", link = "myhello")
L1168 Builder.java generateAndCompile

String target = p.getProperty("global"); // need to set global property.
```

// parser.java
false org.example.presets.HelloPreset :: org.example.hello
Info: Targeting org/example/hello.java

> javac -cp javacpp.jar -d build/ src/main/java/org/example/presets/HelloPreset.java

> java -cp javacpp.jar:build org.bytedeco.javacpp.tools.Builder -Dplatform.includepath=src/main/cpp -Dplatform.linkpath=build/macosx-arm64 -Dglobal=org.example.hello org.example.presets.HelloPreset -d build/macosx-arm64

the classet is suppose to contain: class org.example.presets.HelloPreset

figured out so far:

auto generate .java:

> java -cp javacpp.jar:build org.bytedeco.javacpp.tools.Builder -Dtarget=org.example.hello -Dglobal=org.example.hello  -Dplatform.includepath=src/main/cpp -Dplatform.linkpath=build/macosx-arm64 org.example.presets.HelloPreset -d src/main/java

auto generate cpp/jni:

> java -cp javacpp.jar:build org.bytedeco.javacpp.tools.Builder  -Dplatform.includepath=src/main/cpp -Dplatform.linkpath=build/macosx-arm64 org.example.presets.HelloPreset -d build/macosx-arm64

Exception in thread "main" java.lang.NullPointerException: Cannot invoke "java.util.List.iterator()" because "allInherited" is null
^ have to patch at org.bytedeco.javacpp.tools.Parser.parse(Parser.java:4631)

then the JNI generates but it doesn't contain the method.

# command to run


javac -cp javacpp.jar:src/main/java -d build/ src/main/java/org/example/Main.java

java -Djava.library.path=./build/macosx-arm64/ -cp build:javacpp-platform-1.5.10-bin/javacpp.jar org.example.Main

error:
java -Djava.library.path=./build/macosx-arm64/ -cp build:javacpp-platform-1.5.10-bin/javacpp.jar org.example.Main
Exception in thread "main" java.lang.UnsatisfiedLinkError: 'void org.example.hello.printHelloWorld2()'
	at org.example.hello.printHelloWorld2(Native Method)
	at org.example.Main.main(Main.java:8)


  ```
  nm -gU macosx-arm64/libmyhello.dylib
0000000000003108 T __Z16printHelloWorld2v
```

The compiled dylib doesn't contain the __Z16printHelloWorld2v method.
replaing with a dylib from gradle or maven works.

```
nm -gU macosx-arm64/libjniHelloPreset.dylib
0000000000004724 T _JNI_OnLoad
0000000000005060 T _JNI_OnLoad_jnijavacpp
0000000000004f34 T _JNI_OnUnload
00000000000058c4 T _JNI_OnUnload_jnijavacpp
000000000000ad60 T _Java_org_bytedeco_javacpp_BoolPointer_allocateArray
000000000000abf0 T _Java_org_bytedeco_javacpp_BoolPointer_get
000000000000ac98 T _Java_org_bytedeco_javacpp_BoolPointer_put
000000000000a91c T _Java_org_bytedeco_javacpp_BooleanPointer_allocateArray
000000000000a6a0 T _Java_org_bytedeco_javacpp_BooleanPointer_get__J
000000000000a58c T _Java_org_bytedeco_javacpp_BooleanPointer_get___3ZII
000000000000a748 T _Java_org_bytedeco_javacpp_BooleanPointer_put__JZ
000000000000a808 T _Java_org_bytedeco_javacpp_BooleanPointer_put___3ZII
00000000000079dc T _Java_org_bytedeco_javacpp_BytePointer_allocateArray
0000000000007cc0 T _Java_org_bytedeco_javacpp_BytePointer_getBool
000000000000724c T _Java_org_bytedeco_javacpp_BytePointer_getChar
00000000000077ec T _Java_org_bytedeco_javacpp_BytePointer_getDouble
0000000000007684 T _Java_org_bytedeco_javacpp_BytePointer_getFloat
00000000000073b4 T _Java_org_bytedeco_javacpp_BytePointer_getInt
000000000000751c T _Java_org_bytedeco_javacpp_BytePointer_getLong
0000000000007a44 T _Java_org_bytedeco_javacpp_BytePointer_getPointerValue
00000000000070dc T _Java_org_bytedeco_javacpp_BytePointer_getShort
0000000000006e58 T _Java_org_bytedeco_javacpp_BytePointer_get__J
0000000000006d44 T _Java_org_bytedeco_javacpp_BytePointer_get___3BII
0000000000007d68 T _Java_org_bytedeco_javacpp_BytePointer_putBool
00000000000072f4 T _Java_org_bytedeco_javacpp_BytePointer_putChar
0000000000007894 T _Java_org_bytedeco_javacpp_BytePointer_putDouble
000000000000772c T _Java_org_bytedeco_javacpp_BytePointer_putFloat
000000000000745c T _Java_org_bytedeco_javacpp_BytePointer_putInt
00000000000075c4 T _Java_org_bytedeco_javacpp_BytePointer_putLong
0000000000007b38 T _Java_org_bytedeco_javacpp_BytePointer_putPointerValue
000000000000718c T _Java_org_bytedeco_javacpp_BytePointer_putShort
0000000000006f08 T _Java_org_bytedeco_javacpp_BytePointer_put__JB
0000000000006fc8 T _Java_org_bytedeco_javacpp_BytePointer_put___3BII
0000000000007e30 T _Java_org_bytedeco_javacpp_BytePointer_strcat
0000000000007f14 T _Java_org_bytedeco_javacpp_BytePointer_strchr
0000000000008038 T _Java_org_bytedeco_javacpp_BytePointer_strcmp
0000000000008108 T _Java_org_bytedeco_javacpp_BytePointer_strcoll
00000000000081d8 T _Java_org_bytedeco_javacpp_BytePointer_strcpy
00000000000082bc T _Java_org_bytedeco_javacpp_BytePointer_strcspn
0000000000007954 T _Java_org_bytedeco_javacpp_BytePointer_strerror
0000000000007c3c T _Java_org_bytedeco_javacpp_BytePointer_strlen
000000000000838c T _Java_org_bytedeco_javacpp_BytePointer_strncat
0000000000008478 T _Java_org_bytedeco_javacpp_BytePointer_strncmp
0000000000008558 T _Java_org_bytedeco_javacpp_BytePointer_strncpy
0000000000008644 T _Java_org_bytedeco_javacpp_BytePointer_strpbrk
00000000000087c0 T _Java_org_bytedeco_javacpp_BytePointer_strrchr
00000000000088e4 T _Java_org_bytedeco_javacpp_BytePointer_strspn
00000000000089b4 T _Java_org_bytedeco_javacpp_BytePointer_strstr
0000000000008b30 T _Java_org_bytedeco_javacpp_BytePointer_strtok
0000000000008cac T _Java_org_bytedeco_javacpp_BytePointer_strxfrm
000000000000af30 T _Java_org_bytedeco_javacpp_CLongPointer_allocateArray
000000000000adc8 T _Java_org_bytedeco_javacpp_CLongPointer_get
000000000000ae70 T _Java_org_bytedeco_javacpp_CLongPointer_put
000000000000a520 T _Java_org_bytedeco_javacpp_CharPointer_allocateArray
000000000000a190 T _Java_org_bytedeco_javacpp_CharPointer_get__J
000000000000a238 T _Java_org_bytedeco_javacpp_CharPointer_get___3CII
000000000000a34c T _Java_org_bytedeco_javacpp_CharPointer_put__JC
000000000000a40c T _Java_org_bytedeco_javacpp_CharPointer_put___3CII
000000000000a120 T _Java_org_bytedeco_javacpp_DoublePointer_allocateArray
0000000000009d90 T _Java_org_bytedeco_javacpp_DoublePointer_get__J
0000000000009e38 T _Java_org_bytedeco_javacpp_DoublePointer_get___3DII
000000000000a060 T _Java_org_bytedeco_javacpp_DoublePointer_put__JD
0000000000009f4c T _Java_org_bytedeco_javacpp_DoublePointer_put___3DII
0000000000009d20 T _Java_org_bytedeco_javacpp_FloatPointer_allocateArray
0000000000009990 T _Java_org_bytedeco_javacpp_FloatPointer_get__J
0000000000009a38 T _Java_org_bytedeco_javacpp_FloatPointer_get___3FII
0000000000009c60 T _Java_org_bytedeco_javacpp_FloatPointer_put__JF
0000000000009b4c T _Java_org_bytedeco_javacpp_FloatPointer_put___3FII
0000000000009520 T _Java_org_bytedeco_javacpp_IntPointer_allocateArray
0000000000009190 T _Java_org_bytedeco_javacpp_IntPointer_get__J
0000000000009238 T _Java_org_bytedeco_javacpp_IntPointer_get___3III
000000000000934c T _Java_org_bytedeco_javacpp_IntPointer_put__JI
000000000000940c T _Java_org_bytedeco_javacpp_IntPointer_put___3III
0000000000005d0c T _Java_org_bytedeco_javacpp_Loader_00024Helper_accessGlobalRef
0000000000006018 T _Java_org_bytedeco_javacpp_Loader_00024Helper_addressof
0000000000005d90 T _Java_org_bytedeco_javacpp_Loader_00024Helper_deleteGlobalRef
0000000000006108 T _Java_org_bytedeco_javacpp_Loader_00024Helper_getJavaVM
0000000000005e14 T _Java_org_bytedeco_javacpp_Loader_00024Helper_loadGlobal
0000000000005b7c T _Java_org_bytedeco_javacpp_Loader_00024Helper_newGlobalRef
0000000000005fd4 T _Java_org_bytedeco_javacpp_Loader_00024Helper_totalChips
0000000000005f90 T _Java_org_bytedeco_javacpp_Loader_00024Helper_totalCores
0000000000005b38 T _Java_org_bytedeco_javacpp_Loader_00024Helper_totalProcessors
0000000000009920 T _Java_org_bytedeco_javacpp_LongPointer_allocateArray
0000000000009590 T _Java_org_bytedeco_javacpp_LongPointer_get__J
0000000000009638 T _Java_org_bytedeco_javacpp_LongPointer_get___3JII
0000000000009860 T _Java_org_bytedeco_javacpp_LongPointer_put__JJ
000000000000974c T _Java_org_bytedeco_javacpp_LongPointer_put___3JII
000000000000ab80 T _Java_org_bytedeco_javacpp_PointerPointer_allocateArray
000000000000a984 T _Java_org_bytedeco_javacpp_PointerPointer_get
000000000000aa7c T _Java_org_bytedeco_javacpp_PointerPointer_put
0000000000006188 T _Java_org_bytedeco_javacpp_Pointer_00024NativeDeallocator_deallocate
000000000000619c T _Java_org_bytedeco_javacpp_Pointer_allocate
0000000000006418 T _Java_org_bytedeco_javacpp_Pointer_asDirectBuffer
0000000000006644 T _Java_org_bytedeco_javacpp_Pointer_availablePhysicalBytes
00000000000068c8 T _Java_org_bytedeco_javacpp_Pointer_calloc
0000000000006394 T _Java_org_bytedeco_javacpp_Pointer_free
00000000000066a8 T _Java_org_bytedeco_javacpp_Pointer_getDirectBufferAddress
0000000000006840 T _Java_org_bytedeco_javacpp_Pointer_malloc
0000000000006a78 T _Java_org_bytedeco_javacpp_Pointer_memchr
0000000000006b78 T _Java_org_bytedeco_javacpp_Pointer_memcmp
00000000000066c0 T _Java_org_bytedeco_javacpp_Pointer_memcpy
0000000000006c58 T _Java_org_bytedeco_javacpp_Pointer_memmove
00000000000067ac T _Java_org_bytedeco_javacpp_Pointer_memset
00000000000065b4 T _Java_org_bytedeco_javacpp_Pointer_physicalBytes
0000000000006564 T _Java_org_bytedeco_javacpp_Pointer_physicalBytesInaccurate
0000000000006954 T _Java_org_bytedeco_javacpp_Pointer_realloc
0000000000006604 T _Java_org_bytedeco_javacpp_Pointer_totalPhysicalBytes
00000000000066b8 T _Java_org_bytedeco_javacpp_Pointer_trimMemory
0000000000009124 T _Java_org_bytedeco_javacpp_ShortPointer_allocateArray
0000000000008d8c T _Java_org_bytedeco_javacpp_ShortPointer_get__J
0000000000008e3c T _Java_org_bytedeco_javacpp_ShortPointer_get___3SII
0000000000009064 T _Java_org_bytedeco_javacpp_ShortPointer_put__JS
0000000000008f50 T _Java_org_bytedeco_javacpp_ShortPointer_put___3SII
000000000000b108 T _Java_org_bytedeco_javacpp_SizeTPointer_allocateArray
000000000000afa0 T _Java_org_bytedeco_javacpp_SizeTPointer_get
000000000000b048 T _Java_org_bytedeco_javacpp_SizeTPointer_put
```