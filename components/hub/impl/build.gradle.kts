plugins {
    id("flipper.android-compose")
    id("flipper.anvil")
    id("kotlinx-serialization")
}

android.namespace = "com.flipperdevices.hub.impl"

dependencies {
    implementation(projects.components.hub.api)
    implementation(projects.components.faphub.maincard.api)
    implementation(projects.components.faphub.main.api)
    implementation(projects.components.analytics.metric.api)

    implementation(projects.components.core.di)
    implementation(projects.components.core.preference)
    implementation(projects.components.core.ui.res)
    implementation(projects.components.core.ui.ktx)
    implementation(projects.components.core.ui.theme)
    implementation(projects.components.core.ui.navigation)
    implementation(projects.components.core.ui.decompose)

    implementation(libs.appcompat)

    implementation(projects.components.nfc.attack.api)
    implementation(projects.components.screenstreaming.api)
    implementation(projects.components.deeplink.api)
    implementation(projects.components.bottombar.api)

    // Compose
    implementation(libs.compose.ui)
    implementation(libs.compose.tooling)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material)
    implementation(libs.compose.navigation)
    implementation(libs.bundles.decompose)
    implementation(libs.kotlin.immutable.collections)
}
