plugins {
    id("flipper.android-compose")
    id("flipper.anvil")
}

android.namespace = "com.flipperdevices.screenstreaming.noop"

dependencies {
    implementation(projects.components.screenstreaming.api)
    implementation(projects.components.core.ui.decompose)

    implementation(libs.compose.ui)
    implementation(libs.bundles.decompose)

    implementation(projects.components.core.di)
}
