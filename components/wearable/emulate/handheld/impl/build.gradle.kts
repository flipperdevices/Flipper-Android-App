plugins {
    id("flipper.android-lib")
    id("com.squareup.anvil")
    id("kotlin-kapt")
}

android.namespace = "com.flipperdevices.wearable.emulate.handheld.impl"

dependencies {
    implementation(projects.components.wearable.emulate.common)

    implementation(projects.components.core.di)
    implementation(projects.components.core.log)
    implementation(projects.components.core.ktx)
    implementation(projects.components.core.ui.res)

    implementation(projects.components.keyscreen.api)
    implementation(projects.components.bridge.api)
    implementation(projects.components.bridge.dao.api)
    implementation(projects.components.bridge.service.api)
    implementation(projects.components.bridge.pbutils)

    implementation(libs.kotlin.coroutines)
    implementation(libs.kotlin.coroutines.play.services)

    implementation(libs.wear)
    implementation(libs.wear.gms)

    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.lifecycle.service)

    // Dagger deps
    implementation(libs.dagger)
    kapt(libs.dagger.kapt)
}
