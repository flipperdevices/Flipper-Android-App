import dev.detekt.gradle.Detekt
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.gradle.kotlin.dsl.withType

plugins {
    id("flipper.multiplatform")
    id("flipper.multiplatform-dependencies")
    alias(libs.plugins.wire)
}

android.namespace = "com.flipperdevices.bridge.connection.pbutils"

val wireOutputDir = layout.buildDirectory.dir("generated/source/wire")

wire {
    sourcePath {
        srcDir(file("$rootDir/components/bridge/pbutils/src/main/proto"))
    }
    kotlin {
        out = wireOutputDir.get().asFile.path
        enumMode = "sealed_class"
    }
}

configure<KotlinMultiplatformExtension> {
    sourceSets.named("commonMain") {
        kotlin.srcDir(wireOutputDir)
    }
}

commonDependencies {
    implementation(projects.components.core.ktx)
    implementation(libs.okio)
}

tasks.withType<Detekt> {
    enabled = false
}
