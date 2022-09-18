import com.google.protobuf.gradle.ProtobufConvention
import com.google.protobuf.gradle.generateProtoTasks
import com.google.protobuf.gradle.id
import com.google.protobuf.gradle.protobuf
import com.google.protobuf.gradle.protoc

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
        getByName("main").java {
            // https://github.com/google/protobuf-gradle-plugin/issues/109
            val protobufConvention = project.convention.getPlugin(ProtobufConvention::class.java)
            srcDir("${protobufConvention.protobuf.generatedFilesBaseDir}/main/java")
            srcDir("${protobufConvention.protobuf.generatedFilesBaseDir}/main/kotlin")
        }
    }
}
