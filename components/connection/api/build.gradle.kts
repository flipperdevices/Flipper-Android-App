plugins {
    id("flipper.multiplatform-compose")
    id("flipper.multiplatform-dependencies")
}

android.namespace = "com.flipperdevices.connection.api"

androidDependencies {
    implementation(projects.components.bottombar.api)

    implementation(libs.decompose)
}
