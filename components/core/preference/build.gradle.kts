import com.google.protobuf.gradle.id

plugins {
    id("flipper.lint")
    id("flipper.android-lib")
    id("com.squareup.anvil")
    id("com.google.protobuf")
    id("kotlin-kapt")
}

dependencies {
    implementation(projects.components.core.di)
    implementation(projects.components.core.share)
    api(libs.protobuf.kotlinlite)

    api(libs.datastore)

    // Dagger deps
    implementation(libs.dagger)
    kapt(libs.dagger.kapt)
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
