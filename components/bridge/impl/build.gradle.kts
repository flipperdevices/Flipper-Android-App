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

    implementation(projects.components.bridge.api)
    implementation(projects.components.bridge.protobuf)

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

    // Protobuf jvm
    implementation(libs.protobuf.jvm)

    // Testing
    testImplementation(projects.components.core.test)
    testImplementation(libs.junit)
    testImplementation(libs.mockito.kotlin)
    testImplementation(libs.ktx.testing)
    testImplementation(libs.roboelectric)
    testImplementation(libs.lifecycle.test)
    testImplementation(libs.kotlin.coroutines.test)
}
