import com.google.protobuf.gradle.id

plugins {
    id("flipper.lint")
    id("flipper.android-lib")
    id("com.google.protobuf")
}

dependencies {
    implementation(projects.components.core.log)
    implementation(libs.kotlin.coroutines)
    api(libs.protobuf.kotlinlite)
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
