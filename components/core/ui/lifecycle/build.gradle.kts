plugins {
    id("flipper.multiplatform-compose")
    id("flipper.multiplatform-dependencies")
}

android.namespace = "com.flipperdevices.core.ui.lifecycle"

commonDependencies {
    implementation(projects.components.core.ktx)
    implementation(projects.components.core.log)

    // Compose
    implementation(libs.compose.ui)
    implementation(libs.compose.foundation)

    implementation(projects.components.bridge.connection.feature.provider.api)

    api(libs.decompose)
    implementation(libs.kotlin.coroutines)
    api(libs.essenty.lifecycle)
    implementation(libs.essenty.lifecycle.coroutines)
}

androidDependencies {
    implementation(projects.components.bridge.service.api)
    implementation(libs.annotations)
}
