plugins {
    id("flipper.multiplatform-compose")
    id("flipper.multiplatform-dependencies")
}

android.namespace = "com.flipperdevices.core.ui.ktx"

commonDependencies {
    implementation(projects.components.bridge.dao.api)
    implementation(projects.components.core.ui.theme)
    implementation(projects.components.core.ui.res)

    // Compose
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.lifecycle.compose)
    implementation(libs.compose.placeholder)

    implementation(libs.coil.compose)
    implementation(libs.coil.svg)
    implementation(libs.coil.network)

    implementation(libs.decompose)
}

androidDependencies {
    implementation(libs.image.lottie)
}
