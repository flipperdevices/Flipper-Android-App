import com.google.protobuf.gradle.generateProtoTasks
import com.google.protobuf.gradle.id
import com.google.protobuf.gradle.proto
import com.google.protobuf.gradle.protobuf
import com.google.protobuf.gradle.protoc

plugins {
    id("com.android.library")
    id("kotlin-android")
    id("com.google.protobuf")
}
apply<com.flipperdevices.gradle.ConfigurationPlugin>()

dependencies {
    implementation(project(":components:core"))
    api(Libs.PROTOBUF_JAVALITE)
    api(Libs.PROTOBUF_KOTLIN) {
        exclude(group = Libs.PROTOBUF_GROUP)
    }
}

protobuf {
    protoc {
        artifact = Libs.PROTOBUF_PROTOC
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
