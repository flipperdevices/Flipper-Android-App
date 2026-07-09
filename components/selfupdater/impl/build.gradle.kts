plugins {
    id("flipper.multiplatform")
    id("flipper.multiplatform-dependencies")
    id("flipper.anvil")
}

android.namespace = "com.flipperdevices.selfupdater.impl"

androidDependencies {
    implementation(projects.components.selfupdater.api)
    implementation(projects.components.inappnotification.api)

    implementation(projects.components.core.log)
    implementation(projects.components.core.ktx)
    implementation(projects.components.core.data)

    implementation(libs.lifecycle.runtime.ktx)

    // Dagger deps
    implementation(projects.components.core.di)
}
