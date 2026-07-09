plugins {
    id("flipper.multiplatform-compose")
    id("flipper.multiplatform-dependencies")
    id("flipper.anvil")
    id("kotlinx-serialization")
}


androidDependencies {
    implementation(projects.components.archive.api)
    implementation(projects.components.archive.shared)

    implementation(projects.components.core.ktx)
    implementation(projects.components.core.di)
    implementation(projects.components.core.activityholder)
    implementation(projects.components.core.ui.res)
    implementation(projects.components.core.ui.ktx)
    implementation(projects.components.core.ui.theme)
    implementation(projects.components.core.ui.dialog)
    implementation(projects.components.core.ui.decompose)
    implementation(projects.components.core.ui.lifecycle)

    implementation(projects.components.bridge.dao.api)
    implementation(projects.components.bridge.synchronization.api)
    implementation(projects.components.keyscreen.api)
    implementation(projects.components.keyparser.api)
    implementation(projects.components.deeplink.api)
    implementation(projects.components.rootscreen.api)

    implementation(projects.components.core.preference)

    implementation(libs.appcompat)

    // Compose
    implementation(libs.bundles.decompose)

    implementation(libs.lifecycle.compose)

    implementation(libs.kotlin.immutable.collections)
    implementation(libs.kotlin.serialization.json)
}
