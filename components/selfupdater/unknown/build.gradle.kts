plugins {
    id("flipper.multiplatform")
    id("flipper.multiplatform-dependencies")
    id("flipper.anvil")
}

android.namespace = "com.flipperdevices.selfupdater.unknown"

androidDependencies {
    implementation(projects.components.selfupdater.api)

    implementation(projects.components.core.di)
}
