plugins {
    id("flipper.android-lib")
    id("com.squareup.anvil")
    id("kotlin-kapt")
}

android.namespace = "com.flipperdevices.faphub.installation.manifest.impl"

dependencies {
    implementation(projects.components.faphub.installation.manifest.api)

    implementation(projects.components.core.di)
    implementation(projects.components.core.ktx)
    implementation(projects.components.core.data)
    implementation(projects.components.core.log)

    implementation(projects.components.faphub.dao.api)

    implementation(projects.components.bridge.api)
    implementation(projects.components.bridge.dao.api)
    implementation(projects.components.bridge.service.api)
    implementation(projects.components.bridge.pbutils)

    implementation(libs.kotlin.coroutines)

    // Dagger deps
    implementation(libs.dagger)
    kapt(libs.dagger.kapt)
    implementation(libs.tangle.viewmodel.compose)
    implementation(libs.tangle.viewmodel.api)
    anvil(libs.tangle.viewmodel.compiler)
}
