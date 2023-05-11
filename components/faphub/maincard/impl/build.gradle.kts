plugins {
    id("flipper.android-compose")
    id("com.squareup.anvil")
    id("kotlin-kapt")
}

android.namespace = "com.flipperdevices.faphub.maincard.impl"

dependencies {
    implementation(projects.components.faphub.maincard.api)
    implementation(projects.components.faphub.dao.api)
    implementation(projects.components.faphub.appcard.composable)

    implementation(projects.components.core.di)
    implementation(projects.components.core.log)
    implementation(projects.components.core.ui.res)
    implementation(projects.components.core.ui.ktx)
    implementation(projects.components.core.ui.theme)

    // Compose
    implementation(libs.compose.ui)
    implementation(libs.compose.tooling)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material)

    implementation(libs.lifecycle.compose)
    implementation(libs.lifecycle.viewmodel.ktx)

    // Dagger deps
    implementation(libs.dagger)
    kapt(libs.dagger.kapt)
    implementation(libs.tangle.viewmodel.compose)
    implementation(libs.tangle.viewmodel.api)
    anvil(libs.tangle.viewmodel.compiler)
}
