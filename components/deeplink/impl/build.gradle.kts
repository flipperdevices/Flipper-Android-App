plugins {
    id("flipper.android-lib")
    id("com.squareup.anvil")
    id("kotlin-kapt")
}

dependencies {
    implementation(projects.components.core.di)
    implementation(projects.components.core.ktx)
    implementation(projects.components.core.log)
    implementation(projects.components.core.ktx)

    implementation(projects.components.bridge.dao.api)

    implementation(projects.components.deeplink.api)
    implementation(projects.components.filemanager.api)
    implementation(projects.components.share.api)

    implementation(libs.annotations)
    implementation(libs.appcompat)
    implementation(libs.kotlin.coroutines)
    implementation(libs.cicerone)
    implementation(libs.compose.navigation)

    // Dagger deps
    implementation(libs.dagger)
    kapt(libs.dagger.kapt)
}
