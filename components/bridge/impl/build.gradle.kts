plugins {
    id("flipper.android-lib")
    id("flipper.anvil")
}

android.namespace = "com.flipperdevices.bridge"

dependencies {
    implementation(projects.components.core.log)
    implementation(projects.components.core.ktx)
    implementation(projects.components.core.di)
    implementation(projects.components.core.preference)
    implementation(projects.components.core.data)
    implementation(projects.components.analytics.shake2report.api)
    implementation(projects.components.analytics.metric.api)

    implementation(projects.components.bridge.api)
    implementation(projects.components.bridge.service.api)
    implementation(projects.components.bridge.pbutils)
    implementation(projects.components.bridge.rpcinfo.api)

    implementation(libs.kotlin.coroutines)
    implementation(libs.kotlin.immutable.collections)
    implementation(libs.annotations)
    implementation(libs.ktx)
    implementation(libs.appcompat)

    implementation(libs.ble.scan)
    implementation(libs.ble)
    implementation(libs.ble.common)

    implementation(libs.fastutil)

    // Testing
    testImplementation(projects.components.core.test)
    testImplementation(projects.components.core.buildKonfig)
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.mockito.kotlin)
    testImplementation(libs.ktx.testing)
    testImplementation(libs.roboelectric)
    testImplementation(libs.lifecycle.test)
    testImplementation(libs.kotlin.coroutines.test)
}
