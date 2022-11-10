plugins {
    id("flipper.lint")
    id("flipper.android-lib")
    id("com.squareup.anvil")
    id("kotlin-kapt")
}

dependencies {
    implementation(projects.components.share.api)

    implementation(projects.components.core.di)
    implementation(projects.components.core.log)
    implementation(projects.components.core.ktx)
    implementation(projects.components.core.navigation)
    implementation(projects.components.core.ui.ktx)
    implementation(projects.components.core.ui.res)
    implementation(projects.components.core.ui.fragment)
    implementation(projects.components.core.ui.theme)
    implementation(projects.components.bridge.dao.api)

    implementation(libs.cicerone)
    implementation(libs.appcompat)

    implementation(libs.kotlin.coroutines)

    // Dagger deps
    implementation(libs.dagger)
    kapt(libs.dagger.kapt)
}
