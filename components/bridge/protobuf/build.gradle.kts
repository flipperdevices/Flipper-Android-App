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
    api(Libs.PROTOBUF_JAVALITE)
    api(Libs.PROTOBUF_KOTLIN) {
        exclude(group = Libs.PROTOBUF_GROUP)
    }

    implementation(projects.components.core.log)
    implementation(libs.kotlin.coroutines)
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
