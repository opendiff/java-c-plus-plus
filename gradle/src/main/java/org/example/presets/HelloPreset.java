// file: src/main/java/org/example/presets/HelloPreset.java
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
