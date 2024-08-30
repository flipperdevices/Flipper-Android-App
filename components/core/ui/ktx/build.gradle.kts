plugins {
    id("flipper.multiplatform-compose")
    id("flipper.multiplatform-dependencies")
}

android.namespace = "com.flipperdevices.core.ui.ktx"

commonDependencies {
    implementation(projects.components.bridge.dao.api)
    implementation(projects.components.core.ui.theme)

    // Compose
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.lifecycle.compose)
    implementation(libs.compose.placeholder)

    implementation(libs.decompose)
}

androidDependencies {
    implementation(projects.components.core.ui.res)
    implementation(libs.image.lottie)
    implementation(libs.coil.compose)
    implementation(libs.coil.svg)
}
