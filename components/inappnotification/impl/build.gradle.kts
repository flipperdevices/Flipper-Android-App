plugins {
    id("flipper.multiplatform-compose")
    id("flipper.multiplatform-dependencies")
    id("flipper.anvil")
}

androidDependencies {
    implementation(projects.components.inappnotification.api)
    implementation(projects.components.core.di)
    implementation(projects.components.core.log)
    implementation(projects.components.core.ktx)
    implementation(projects.components.core.ui.res)
    implementation(projects.components.core.ui.theme)
    implementation(projects.components.core.ui.ktx)

    implementation(projects.components.deeplink.api)
    implementation(projects.components.rootscreen.api)

    implementation(libs.appcompat)
    implementation(libs.kotlin.coroutines)

    // Compose
    implementation(libs.image.lottie)
}
