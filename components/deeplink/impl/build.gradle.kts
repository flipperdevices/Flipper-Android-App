plugins {
    id("flipper.lint")
    id("androidLibrary")
    id("com.squareup.anvil")
    kotlin("kapt")
}

dependencies {
    implementation(projects.components.core.di)
    implementation(projects.components.core.log)

    implementation(projects.components.bridge.dao.api)

    implementation(projects.components.deeplink.api)
    implementation(projects.components.filemanager.api)
    implementation(projects.components.share.api)

    implementation(libs.annotations)
    implementation(libs.appcompat)
    implementation(libs.kotlin.coroutines)
    implementation(libs.cicerone)

    // Dagger deps
    implementation(libs.dagger)
    kapt(libs.dagger.kapt)
}
