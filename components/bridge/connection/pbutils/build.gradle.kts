import io.gitlab.arturbosch.detekt.Detekt
import org.gradle.kotlin.dsl.withType

plugins {
    id("flipper.multiplatform")
    id("flipper.multiplatform-dependencies")
    alias(libs.plugins.wire)
}

android.namespace = "com.flipperdevices.bridge.connection.pbutils"

wire {
    sourcePath {
        srcDir(file("$rootDir/components/bridge/pbutils/src/main/proto"))
    }
    kotlin {
        enumMode = "sealed_class"
    }
}

commonDependencies {
    implementation(projects.components.core.ktx)
    implementation(libs.okio)
}

tasks.withType<Detekt> {
    enabled = false
}

