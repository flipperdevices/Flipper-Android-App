import com.google.protobuf.gradle.id

plugins {
    id("flipper.lint")
    id("flipper.android-compose")
    id("kotlin-parcelize")
    id("com.google.protobuf")
}

dependencies {
    api(libs.protobuf.kotlinlite)

    implementation(projects.components.core.ktx)

    implementation(libs.wear.gms)

    implementation(libs.compose.ui)
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
