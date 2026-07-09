plugins {
    id("flipper.multiplatform")
    id("flipper.multiplatform-dependencies")
    id("kotlinx-serialization")
}

android.namespace = "com.flipperdevices.faphub.report.api"

androidDependencies {
    implementation(projects.components.core.ui.decompose)

    implementation(libs.decompose)

    implementation(libs.kotlin.serialization.json)
}
