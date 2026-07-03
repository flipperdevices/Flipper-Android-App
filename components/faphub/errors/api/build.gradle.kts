plugins {
    id("flipper.multiplatform-compose")
    id("flipper.multiplatform-dependencies")
}

android.namespace = "com.flipperdevices.faphub.errors"

androidDependencies {
    implementation(projects.components.core.ui.theme)

    implementation(projects.components.faphub.installation.manifest.api)

    // Compose

    implementation(libs.ktor.serialization)
    implementation(libs.ktor.client)
}
