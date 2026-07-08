plugins {
    id("flipper.multiplatform")
    id("flipper.multiplatform-dependencies")
    id("flipper.anvil")
}

android.namespace = "com.flipperdevices.bridge"

androidDependencies {
    implementation(projects.components.core.log)
    implementation(projects.components.core.ktx)
    implementation(projects.components.core.di)
    implementation(projects.components.core.preference)
    implementation(projects.components.core.data)
    implementation(projects.components.analytics.shake2report.api)
    implementation(projects.components.analytics.metric.api)

    implementation(projects.components.firstpair.connection.api)

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
}

dependencies {
    "androidUnitTestImplementation"(projects.components.core.test)
    "androidUnitTestImplementation"(projects.components.core.buildKonfig)
    "androidUnitTestImplementation"(libs.junit)
    "androidUnitTestImplementation"(libs.mockk)
    "androidUnitTestImplementation"(libs.mockito.kotlin)
    "androidUnitTestImplementation"(libs.ktx.testing)
    "androidUnitTestImplementation"(libs.roboelectric)
    "androidUnitTestImplementation"(libs.lifecycle.test)
    "androidUnitTestImplementation"(libs.kotlin.coroutines.test)
}
