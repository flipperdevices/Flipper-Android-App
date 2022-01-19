plugins {
    androidLibrary
    id("com.squareup.anvil")
    kotlin("kapt")
}

dependencies {
    implementation(projects.components.core.di)
    implementation(projects.components.core.log)

    implementation(projects.components.bridge.dao.api)

    implementation(projects.components.deeplink.api)
    implementation(projects.components.filemanager.api)

    implementation(libs.annotations)
    implementation(libs.appcompat)
    implementation(libs.kotlin.coroutines)

    // Dagger deps
    implementation(libs.dagger)
    kapt(libs.dagger.kapt)
}
