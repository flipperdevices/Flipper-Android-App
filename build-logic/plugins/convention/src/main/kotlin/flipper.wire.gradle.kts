plugins {
    id("com.squareup.wire")
}

wire {
    sourcePath {
        srcDir("$projectDir/src/main/proto")
    }
    kotlin {
        enumMode = "sealed_class"
    }
}
