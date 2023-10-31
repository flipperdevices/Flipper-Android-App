plugins {
    id("flipper.android-compose")
    id("flipper.anvil")
}

android.namespace = "com.flipperdevices.notification.impl"

dependencies {
    implementation(projects.components.notification.api)

    implementation(projects.components.core.ui.res)
    implementation(projects.components.core.di)
    implementation(projects.components.core.log)

    // Compose
    implementation(libs.compose.ui)
    implementation(libs.compose.tooling)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material)
    implementation(libs.compose.navigation)

    implementation(libs.gms.firebase)
}
