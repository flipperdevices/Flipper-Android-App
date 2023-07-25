plugins {
    id("flipper.android-compose")
    id("flipper.anvil")
}

android.namespace = "com.flipperdevices.core.ui.errors"

dependencies {
    implementation(projects.components.faphub.errors.api)

    implementation(projects.components.core.di)
    implementation(projects.components.core.ui.res)
    implementation(projects.components.core.ui.theme)

    implementation(projects.components.bottombar.api)

    // Compose
    implementation(libs.compose.ui)
    implementation(libs.compose.tooling)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material)
    implementation(libs.compose.paging)

    implementation(libs.ktor.client)
}
