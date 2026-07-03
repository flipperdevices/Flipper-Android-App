plugins {
    id("flipper.multiplatform-compose")
    id("flipper.multiplatform-dependencies")
}

android.namespace = "com.flipperdevices.faphub.installedtab.api"

androidDependencies {
    implementation(projects.components.faphub.dao.api)

    // Compose
    implementation(libs.decompose)
}
