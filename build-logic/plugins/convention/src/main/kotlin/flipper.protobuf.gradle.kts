import com.google.protobuf.gradle.id

plugins {
    id("com.google.protobuf")
}

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
