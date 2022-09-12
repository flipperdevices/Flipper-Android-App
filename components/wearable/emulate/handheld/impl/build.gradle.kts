plugins {
    id("flipper.lint")
    id("flipper.android-lib")
    id("com.squareup.anvil")
    id("kotlin-kapt")
}

dependencies {
    implementation(projects.components.wearable.emulate.common)

    implementation(projects.components.core.di)
    implementation(projects.components.core.log)
    implementation(projects.components.core.ktx)

    implementation(projects.components.keyscreen.api)
    implementation(projects.components.bridge.api)
    implementation(projects.components.bridge.dao.api)
    implementation(projects.components.bridge.service.api)
    implementation(projects.components.bridge.pbutils)
    implementation(libs.protobuf.jvm)

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
