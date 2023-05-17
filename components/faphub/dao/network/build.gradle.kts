plugins {
    id("flipper.android-lib")
    id("com.squareup.anvil")
    id("kotlin-kapt")
    id("kotlinx-serialization")
}

android.namespace = "com.flipperdevices.faphub.dao.network"

dependencies {
    implementation(projects.components.faphub.dao.api)

    implementation(projects.components.core.di)
    implementation(projects.components.core.log)
    implementation(projects.components.core.data)

    implementation(libs.ktor.client)
    implementation(libs.ktor.serialization)
    implementation(libs.kotlin.serialization.json)
    implementation(libs.ktor.logging)
    implementation(libs.ktor.negotiation)

    implementation(libs.retrofit)
    implementation(libs.retrofit.json)

    // Dagger deps
    implementation(libs.dagger)
    kapt(libs.dagger.kapt)

    implementation(libs.kotlin.coroutines)
    implementation(libs.kotlin.immutable.collections)

    implementation(libs.annotations)
}
