plugins {
    id("flipper.android-compose")
    id("flipper.anvil")
    id("kotlinx-serialization")
}

android.namespace = "com.flipperdevices.widget.screen"

dependencies {
    implementation(projects.components.widget.api)

    implementation(projects.components.core.di)
    implementation(projects.components.core.log)
    implementation(projects.components.core.ktx)
    implementation(projects.components.core.activityholder)

    implementation(projects.components.core.ui.theme)
    implementation(projects.components.core.ui.ktx)
    implementation(projects.components.core.ui.res)
    implementation(projects.components.core.ui.lifecycle)
    implementation(projects.components.core.ui.decompose)

    implementation(projects.components.bridge.api)
    implementation(projects.components.bridge.synchronization.api)
    implementation(projects.components.bridge.dao.api)

    implementation(libs.appcompat)

    implementation(projects.components.deeplink.api)
    implementation(projects.components.archive.api)
    implementation(projects.components.archive.shared)

    // Compose
    implementation(libs.compose.ui)
    implementation(libs.compose.tooling)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material)
    implementation(libs.compose.swipetorefresh)
    implementation(libs.bundles.decompose)

    implementation(libs.lifecycle.viewmodel.ktx)

    implementation(libs.kotlin.immutable.collections)
    implementation(libs.kotlin.serialization.json)
}
