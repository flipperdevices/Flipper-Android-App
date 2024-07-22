plugins {
    id("flipper.android-compose")
    id("flipper.anvil")
}

android.namespace = "com.flipperdevices.wearable.sync.wear.impl"

dependencies {
    implementation(projects.components.wearable.sync.common)
    implementation(projects.components.wearable.sync.wear.api)

    implementation(projects.components.core.di)
    implementation(projects.components.core.activityholder)
    implementation(projects.components.core.log)
    implementation(projects.components.core.ktx)
    implementation(projects.components.core.ui.res)
    implementation(projects.components.core.ui.theme)
    implementation(projects.components.core.ui.lifecycle)
    implementation(projects.components.core.ui.decompose)
    implementation(projects.components.core.ui.ktx)

    implementation(projects.components.bridge.dao.api)
    implementation(projects.components.wearable.emulate.wear.api)
    implementation(projects.components.wearable.core.ui.components)
    implementation(projects.components.wearable.wearrootscreen.api)

    implementation(libs.wear)
    implementation(libs.wear.gms)
    implementation(libs.wear.interaction.phone)
    implementation(libs.wear.interaction.remote)

    implementation(libs.kotlin.coroutines)
    implementation(libs.kotlin.coroutines.play.services)
    implementation(libs.kotlin.immutable.collections)

    // Compose
    implementation(libs.compose.ui)
    implementation(libs.compose.tooling)
    implementation(libs.compose.wear.foundation)
    implementation(libs.compose.wear.material)
    implementation(libs.compose.wear.preview)
    implementation(libs.bundles.decompose)
    implementation(libs.horologist.layout)
    implementation(libs.lifecycle.compose)
}
