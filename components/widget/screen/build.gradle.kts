plugins {
    id("flipper.multiplatform-compose")
    id("flipper.multiplatform-dependencies")
    id("flipper.anvil")
    id("kotlinx-serialization")
}


androidDependencies {
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

    implementation(projects.components.bridge.synchronization.api)
    implementation(projects.components.bridge.dao.api)

    implementation(libs.appcompat)

    implementation(projects.components.deeplink.api)
    implementation(projects.components.archive.api)
    implementation(projects.components.archive.shared)

    // Compose
    implementation(libs.compose.swipetorefresh)
    implementation(libs.bundles.decompose)

    implementation(libs.kotlin.immutable.collections)
    implementation(libs.kotlin.serialization.json)
}
