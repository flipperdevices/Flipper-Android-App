plugins {
    id("flipper.lint")
    id("flipper.android-lib")
    id("kotlin-android")
    id("kotlin-parcelize")
}

dependencies {
    implementation(projects.components.bridge.pbutils)
    implementation(projects.components.core.log)
    implementation(projects.components.core.ktx)
    implementation(projects.components.core.data)

    implementation(libs.kotlin.coroutines)
    implementation(libs.ble.common)
    implementation(libs.ble.scan)

    implementation(libs.appcompat)

    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.kotlin.coroutines.test)
    testImplementation(libs.roboelectric)
    testImplementation(libs.ktx.testing)
    testImplementation(libs.mockk)
}
