plugins {
    id("flipper.android-lib")
    id("flipper.anvil")
}

android.namespace = "com.flipperdevices.bridge.service.impl"

android {
    testOptions {
        unitTests.isIncludeAndroidResources = true
    }
}

dependencies {
    implementation(projects.components.core.di)
    implementation(projects.components.core.ktx)
    implementation(projects.components.core.log)
    implementation(projects.components.core.ui.lifecycle)
    implementation(projects.components.core.preference)
    implementation(projects.components.core.ui.res)

    implementation(projects.components.analytics.shake2report.api)
    implementation(projects.components.analytics.metric.api)

    implementation(projects.components.unhandledexception.api)

    implementation(projects.components.bridge.api)
    implementation(projects.components.bridge.impl)
    implementation(projects.components.bridge.pbutils)
    implementation(projects.components.bridge.service.api)

    implementation(libs.annotations)
    implementation(libs.appcompat)

    implementation(libs.kotlin.coroutines)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.lifecycle.service)
    implementation(libs.essenty.lifecycle)

    implementation(libs.ble)
    implementation(libs.ble.common)
    implementation(libs.ble.scan)

    // Dagger deps
    implementation(libs.dagger)

    testImplementation(projects.components.core.test)
    testImplementation(projects.components.core.buildKonfig)
    testImplementation(libs.junit)
    testImplementation(libs.mockito.kotlin)
    testImplementation(libs.ktx.testing)
    testImplementation(libs.roboelectric)
    testImplementation(libs.lifecycle.test)
}
