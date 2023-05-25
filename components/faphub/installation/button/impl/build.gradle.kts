plugins {
    id("flipper.android-compose")
    id("com.squareup.anvil")
    id("kotlin-kapt")
}

android.namespace = "com.flipperdevices.faphub.installation.button.impl"

dependencies {
    implementation(projects.components.faphub.installation.button.api)

    implementation(projects.components.core.di)
    implementation(projects.components.core.ui.res)
    implementation(projects.components.core.ui.ktx)
    implementation(projects.components.core.ui.theme)
    implementation(projects.components.core.ktx)
    implementation(projects.components.core.data)

    implementation(projects.components.faphub.installation.stateprovider.api)

    // Compose
    implementation(libs.compose.ui)
    implementation(libs.compose.tooling)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material)

    implementation(projects.components.faphub.dao.api)

    // Dagger deps
    implementation(libs.dagger)
    kapt(libs.dagger.kapt)
    implementation(libs.tangle.viewmodel.compose)
    implementation(libs.tangle.viewmodel.api)
    anvil(libs.tangle.viewmodel.compiler)

    implementation(libs.kotlin.coroutines)
    implementation(libs.lifecycle.viewmodel.ktx)
}
