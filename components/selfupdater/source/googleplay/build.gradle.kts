plugins {
    id("flipper.android-lib")
    id("com.squareup.anvil")
    id("kotlin-kapt")
}

dependencies {
    implementation(projects.components.selfupdater.api)
    implementation(projects.components.inappnotification.api)

    implementation(projects.components.core.log)
    implementation(projects.components.core.activityholder)

    // In-app update
    implementation(libs.app.update)
    implementation(libs.app.update.ktx)

    // ViewModel
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.tangle.viewmodel.compose)
    implementation(libs.tangle.viewmodel.api)
    anvil(libs.tangle.viewmodel.compiler)

    // Dagger deps
    implementation(projects.components.core.di)
    implementation(libs.dagger)
    kapt(libs.dagger.kapt)
}
