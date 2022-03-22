import com.google.protobuf.gradle.generateProtoTasks
import com.google.protobuf.gradle.id
import com.google.protobuf.gradle.protobuf
import com.google.protobuf.gradle.protoc

plugins {
    androidLibrary
    id("com.squareup.anvil")
    id("com.google.protobuf")
    kotlin("kapt")
}

dependencies {
    implementation(projects.components.core.di)
    api(libs.protobuf.javalite)
    api(libs.protobuf.kotlin) {
        @Suppress("UnstableApiUsage")
        exclude(libs.protobuf.kotlin.get().module.group)
    }

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
        all().forEach {
            it.builtins {
                id("java") {
                    option("lite")
                }
                id("kotlin")
            }
        }
    }
}
