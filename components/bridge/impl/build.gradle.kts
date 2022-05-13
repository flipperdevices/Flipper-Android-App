plugins {
    androidLibrary
    id("com.squareup.anvil")
    kotlin("kapt")
}

dependencies {
    implementation(projects.components.core.log)
    implementation(projects.components.core.ktx)
    implementation(projects.components.core.di)
    implementation(projects.components.core.preference)
    implementation(projects.components.analytics.shake2report.api)

    implementation(projects.components.bridge.api)
    implementation(projects.components.bridge.pbutils)
    implementation(libs.protobuf.jvm)

    implementation(libs.kotlin.coroutines)
    implementation(libs.annotations)
    implementation(libs.ktx)
    implementation(libs.appcompat)

    implementation(libs.ble.scan)
    implementation(libs.ble)
    implementation(libs.ble.common)

    implementation(libs.dagger)
    kapt(libs.dagger.kapt)

    implementation(libs.fastutil)

    // Testing
    testImplementation(projects.components.core.test)
    testImplementation(libs.junit)
    testImplementation(libs.mockito.kotlin)
    testImplementation(libs.ktx.testing)
    testImplementation(libs.roboelectric)
    testImplementation(libs.lifecycle.test)
    testImplementation(libs.kotlin.coroutines.test)
}
