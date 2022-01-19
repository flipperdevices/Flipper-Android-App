import com.google.protobuf.gradle.generateProtoTasks
import com.google.protobuf.gradle.id
import com.google.protobuf.gradle.proto
import com.google.protobuf.gradle.protobuf
import com.google.protobuf.gradle.protoc

plugins {
    androidLibrary
    id("com.google.protobuf")
}

dependencies {
    api(libs.protobuf.javalite)
    api(libs.protobuf.kotlin) {
        @Suppress("UnstableApiUsage")
        exclude(libs.protobuf.kotlin.get().module.group)
    }

    implementation(projects.components.core.log)
    implementation(libs.kotlin.coroutines)
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

android {
    sourceSets {
        (getByName("main") as com.android.build.gradle.api.AndroidSourceSet).proto {
            srcDir("$projectDir/flipperzero-protobuf")
        }
    }
}
