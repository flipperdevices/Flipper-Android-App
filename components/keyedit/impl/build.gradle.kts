plugins {
    id("flipper.android-compose")
    id("flipper.anvil")

    id("kotlinx-serialization")
}

android.namespace = "com.flipperdevices.keyedit.impl"

dependencies {
    implementation(projects.components.core.di)
    implementation(projects.components.core.ktx)
    implementation(projects.components.core.log)
    implementation(projects.components.core.storage)
    implementation(projects.components.core.ui.res)
    implementation(projects.components.core.ui.ktx)
    implementation(projects.components.core.ui.theme)
    implementation(projects.components.core.ui.decompose)
    implementation(projects.components.core.ui.lifecycle)

    implementation(projects.components.keyedit.api)
    implementation(projects.components.keyparser.api)

    implementation(projects.components.keyscreen.shared)

    implementation(projects.components.bridge.dao.api)
    implementation(projects.components.bridge.synchronization.api)
    implementation(projects.components.bridge.api)
    implementation(projects.components.inappnotification.api)
    implementation(projects.components.singleactivity.api)
    implementation(projects.components.deeplink.api)

    implementation(libs.appcompat)

    implementation(libs.kotlin.serialization.json)
    implementation(libs.kotlin.immutable.collections)

    // Compose
    implementation(libs.compose.ui)
    implementation(libs.compose.tooling)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material)
    implementation(libs.compose.constraint)
    implementation(libs.bundles.decompose)
    implementation(libs.lifecycle.compose)

    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.lifecycle.runtime.ktx)

    implementation(libs.okio)
}
