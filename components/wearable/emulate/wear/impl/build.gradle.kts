plugins {
    id("flipper.android-compose")
    id("flipper.anvil")
    id("kotlin-kapt")
}

android.namespace = "com.flipperdevices.wearable.emulate.impl"

dependencies {
    implementation(projects.components.wearable.emulate.common)
    implementation(projects.components.wearable.emulate.wear.api)
    implementation(projects.components.wearable.sync.wear.api)

    implementation(projects.components.core.di)
    implementation(projects.components.core.log)
    implementation(projects.components.core.ktx)
    implementation(projects.components.core.preference)
    implementation(projects.components.core.activityholder)
    implementation(projects.components.core.ui.decompose)
    implementation(projects.components.core.ui.theme)
    implementation(projects.components.core.ui.res)
    implementation(projects.components.core.ui.ktx)
    implementation(projects.components.core.ui.lifecycle)
    implementation(projects.components.wearable.core.ui.components)

    implementation(projects.components.bridge.dao.api)
    implementation(projects.components.keyscreen.api)
    implementation(projects.components.keyemulate.api)

    implementation(libs.kotlin.coroutines)
    implementation(libs.kotlin.coroutines.play.services)

    implementation(libs.datastore)

    implementation(libs.wear)
    implementation(libs.wear.gms)
    implementation(libs.wear.interaction.phone)

    // Compose
    implementation(libs.compose.ui)
    implementation(libs.compose.tooling)
    implementation(libs.compose.placeholder)
    implementation(libs.compose.wear.foundation)
    implementation(libs.compose.wear.material)
    implementation(libs.bundles.decompose)
    implementation(libs.horologist.layout)
    implementation(libs.lifecycle.compose)
}
