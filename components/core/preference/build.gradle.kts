import com.google.protobuf.gradle.generateProtoTasks
import com.google.protobuf.gradle.id
import com.google.protobuf.gradle.protobuf
import com.google.protobuf.gradle.protoc

plugins {
    id("androidLibrary")
    id("com.squareup.anvil")
    id("com.google.protobuf")
    kotlin("kapt")
}

dependencies {
    implementation(projects.components.core.di)
    api(libs.protobuf.kotlinlite)

    api(libs.datastore)

    // Dagger deps
    implementation(libs.dagger)
    kapt(libs.dagger.kapt)
}

// Kotlin 1.7.0 breaks kapt processing for protobuf generated java sources
// https://youtrack.jetbrains.com/issue/KT-52761/Kotlin-170-breaks-kapt-processing-for-protobuf-generated-java-sources
android.libraryVariants.all {
    android.sourceSets[this.name].apply {
        java.srcDir(project.file("build/generated/source/proto/${this.name}/java"))
        kotlin.srcDir(project.file("build/generated/source/proto/${this.name}/kotlin"))
    }
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
