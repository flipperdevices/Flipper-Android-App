plugins {
    id("flipper.lint")
    id("flipper.android-lib")
    id("flipper.android-compose")
    id("com.squareup.anvil")
    id("kotlin-kapt")
    id("kotlinx-serialization")
}

dependencies {
    implementation(projects.components.bridge.synchronization.api)

    implementation(projects.components.core.di)
    implementation(projects.components.core.ktx)
    implementation(projects.components.core.log)
    implementation(projects.components.core.preference)
    implementation(projects.components.core.ui.lifecycle)

    implementation(projects.components.bridge.api)
    implementation(projects.components.bridge.pbutils)
    implementation(libs.protobuf.jvm)
    implementation(projects.components.bridge.service.api)
    implementation(projects.components.bridge.dao.api)

    implementation(projects.components.wearable.sync.handheld.api)
    implementation(projects.components.nfc.mfkey32.api)

    implementation(projects.components.analytics.shake2report.api)
    implementation(projects.components.analytics.metric.api)

    // Compose
    implementation(libs.compose.ui)
    implementation(libs.compose.tooling)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material)

    // Dagger deps
    implementation(libs.dagger)
    kapt(libs.dagger.kapt)

    implementation(libs.lifecycle.runtime.ktx)

    implementation(libs.kotlin.serialization.json)

    // Testing
    testImplementation(projects.components.core.test)
    testImplementation(libs.junit)
    testImplementation(libs.mockito.kotlin)
    testImplementation(libs.ktx.testing)
    testImplementation(libs.roboelectric)
    testImplementation(libs.lifecycle.test)
    testImplementation(libs.kotlin.coroutines.test)
}
