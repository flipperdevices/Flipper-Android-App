import com.android.build.gradle.BaseExtension
import com.google.protobuf.gradle.ProtobufConvention
import com.google.protobuf.gradle.generateProtoTasks
import com.google.protobuf.gradle.id
import com.google.protobuf.gradle.protobuf
import com.google.protobuf.gradle.protoc
import org.gradle.accessors.dm.LibrariesForLibs

plugins {
    id("com.google.protobuf")
}

// https://github.com/gradle/gradle/issues/15383
val libs = the<LibrariesForLibs>()
dependencies {
    "api"(libs.protobuf.kotlinlite)
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

configure<BaseExtension> {
    sourceSets {
        getByName("main").java {
            // https://github.com/google/protobuf-gradle-plugin/issues/109
            val protobufConvention = project.convention.getPlugin(ProtobufConvention::class.java)
            srcDir("${protobufConvention.protobuf.generatedFilesBaseDir}/main/java")
            srcDir("${protobufConvention.protobuf.generatedFilesBaseDir}/main/kotlin")
        }
    }
}