plugins {
    id("flipper.multiplatform-compose")
    id("flipper.multiplatform-dependencies")
    id("flipper.anvil")
}


androidDependencies {
    implementation(projects.components.core.di)

    implementation(projects.components.notification.api)
    implementation(projects.components.inappnotification.api)

    // Compose

    implementation(libs.kotlin.coroutines)
    implementation(libs.decompose)
}
