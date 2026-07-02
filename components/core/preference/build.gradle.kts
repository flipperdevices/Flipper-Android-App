import dev.detekt.gradle.Detekt
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.gradle.kotlin.dsl.withType

plugins {
    id("flipper.multiplatform")
    id("flipper.multiplatform-dependencies")
    id("flipper.anvil-multiplatform")
    alias(libs.plugins.wire)
}

android.namespace = "com.flipperdevices.core.preference"

val wireOutputDir = layout.buildDirectory.dir("generated/source/wire")
val generateProtos = tasks.named("generateProtos")

commonDependencies {
    implementation(projects.components.core.di)
    implementation(projects.components.core.storage)

    api(libs.datastore)
}

tasks.withType<Detekt> {
    enabled = false
}

wire {
    sourcePath {
        srcDir("src/commonMain/proto")
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
