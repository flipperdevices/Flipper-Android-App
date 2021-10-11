import com.google.protobuf.gradle.generateProtoTasks
import com.google.protobuf.gradle.id
import com.google.protobuf.gradle.ofSourceSet
import com.google.protobuf.gradle.proto
import com.google.protobuf.gradle.protobuf
import com.google.protobuf.gradle.protoc

plugins {
    id("com.android.library")
    id("kotlin-android")
    id("com.google.protobuf")
}
apply<com.flipper.gradle.ConfigurationPlugin>()

dependencies {
    implementation(project(":components:core"))
    implementation(Libs.PROTOBUF_JAVA)
}

protobuf {
    protoc {
        artifact = Libs.PROTOBUF_PROTOC
    }

    generateProtoTasks {
        ofSourceSet("main").forEach {
            it.plugins {
                id("javalite")
            }
        }
    }
}

android {
    sourceSets {
        (getByName("main") as com.android.build.gradle.api.AndroidSourceSet).proto {
            srcDir("${projectDir}/flipperzero-protobuf")
        }
    }
}