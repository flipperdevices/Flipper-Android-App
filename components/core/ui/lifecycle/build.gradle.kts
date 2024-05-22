plugins {
    id("flipper.multiplatform-compose")
    id("flipper.multiplatform-dependencies")
}

android.namespace = "com.flipperdevices.core.ui.lifecycle"

commonMainDependencies {
    implementation(projects.components.core.ktx)
    implementation(projects.components.core.log)

    // Compose
    implementation(libs.compose.ui)
    implementation(libs.compose.foundation)

    api(libs.decompose)
    implementation(libs.kotlin.coroutines)
    api(libs.essenty.lifecycle)
    implementation(libs.essenty.lifecycle.coroutines)
}

androidMainDependencies {
    implementation(projects.components.bridge.service.api)
    implementation(libs.annotations)
}
