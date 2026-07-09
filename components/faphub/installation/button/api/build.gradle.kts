plugins {
    id("flipper.multiplatform-compose")
    id("flipper.multiplatform-dependencies")
}

android.namespace = "com.flipperdevices.faphub.installation.button.api"

androidDependencies {
    implementation(projects.components.faphub.dao.api)

    implementation(projects.components.core.data)

    // Compose
    implementation(libs.decompose)
}
