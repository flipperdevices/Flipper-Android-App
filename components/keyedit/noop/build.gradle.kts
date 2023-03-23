plugins {
    id("flipper.android-lib")
    id("com.squareup.anvil")
    id("kotlin-kapt")
}

dependencies {
    implementation(projects.components.keyedit.api)

    implementation(projects.components.bridge.dao.api)
    implementation(projects.components.core.di)
    implementation(projects.components.core.ui.navigation)

    implementation(libs.compose.navigation)

    implementation(libs.appcompat)

    // Dagger deps
    implementation(libs.dagger)
    kapt(libs.dagger.kapt)
}
