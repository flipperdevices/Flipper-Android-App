plugins {
    id("flipper.android-compose")
    id("flipper.anvil")
}

android.namespace = "com.flipperdevices.core.ui.theme"

dependencies {
    implementation(projects.components.core.di)
    implementation(projects.components.core.ui.res)
    implementation(projects.components.core.ktx)
    implementation(projects.components.core.preference)

    // Compose
    implementation(libs.compose.ui)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material)

    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.lifecycle.compose)
    implementation(libs.appcompat)

    // Dagger deps
    implementation(libs.tangle.viewmodel.api)
    implementation(libs.tangle.viewmodel.compose)
    anvil(libs.tangle.viewmodel.compiler)
}
