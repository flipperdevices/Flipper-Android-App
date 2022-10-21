import com.google.protobuf.gradle.id
import org.gradle.accessors.dm.LibrariesForLibs

plugins {
    id("com.google.protobuf")
}

// https://github.com/gradle/gradle/issues/15383
val libs = the<LibrariesForLibs>()
dependencies {
    "api"(libs.protobuf.kotlinlite)
}

protobuf {
    protoc {
        artifact = libs.protobuf.protoc.get().toString()
    }

    generateProtoTasks {
        all().forEach { task ->
            task.builtins {
                id("java") {
                    option("lite")
                }
                id("kotlin")
            }
        }
    }
}