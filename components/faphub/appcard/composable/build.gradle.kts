plugins {
    id("flipper.android-compose")
}

android.namespace = "com.flipperdevices.faphub.appcard.composable"

dependencies {
    implementation(projects.components.faphub.dao.api)

    implementation(projects.components.core.ui.ktx)
    implementation(projects.components.core.ui.res)
    implementation(projects.components.core.ui.errors)
    implementation(projects.components.core.ui.theme)

    implementation(libs.kotlin.immutable.collections)

    // Compose
    implementation(libs.compose.ui)
    implementation(libs.compose.tooling)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material)
    implementation(libs.coil.compose)
    implementation(libs.compose.paging)
}
