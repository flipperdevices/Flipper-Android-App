plugins {
    id("flipper.android-compose")
    id("flipper.anvil")
}

android.namespace = "com.flipperdevices.keyemulate.impl"

dependencies {
    implementation(projects.components.keyemulate.api)
    implementation(projects.components.keyparser.api)
    implementation(projects.components.screenstreaming.api)

    implementation(projects.components.core.di)
    implementation(projects.components.core.log)
    implementation(projects.components.core.ktx)
    implementation(projects.components.core.data)
    implementation(projects.components.core.preference)
    implementation(projects.components.core.ui.res)
    implementation(projects.components.core.ui.ktx)
    implementation(projects.components.core.ui.theme)
    implementation(projects.components.core.ui.dialog)
    implementation(projects.components.core.ui.lifecycle)

    implementation(projects.components.bridge.api)
    implementation(projects.components.bridge.service.api)
    implementation(projects.components.bridge.dao.api)
    implementation(projects.components.bridge.pbutils)
    implementation(projects.components.bridge.synchronization.api)

    implementation(projects.components.rootscreen.api)

    // Compose
    implementation(libs.compose.ui)
    implementation(libs.compose.tooling)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material)
    implementation(libs.compose.placeholder)
    implementation(libs.bundles.decompose)

    implementation(libs.image.lottie)

    implementation(libs.appcompat)

    implementation(libs.lifecycle.compose)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.lifecycle.runtime.ktx)

    // Testing
    testImplementation(projects.components.core.test)
    testImplementation(libs.junit)
    testImplementation(libs.kotlin.coroutines.test)
    testImplementation(libs.roboelectric)
    testImplementation(libs.ktx.testing)
    testImplementation(libs.mockk)
}
