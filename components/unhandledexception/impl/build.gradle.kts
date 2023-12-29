plugins {
    id("flipper.android-compose")
    id("flipper.anvil")
}

android.namespace = "com.flipperdevices.unhandledexception.impl"

dependencies {
    implementation(projects.components.unhandledexception.api)

    implementation(projects.components.core.di)
    implementation(projects.components.core.preference)
    implementation(projects.components.core.log)
    implementation(projects.components.core.ui.theme)
    implementation(projects.components.core.ui.dialog)

    implementation(projects.components.bridge.api)
    implementation(projects.components.bridge.service.api)

    implementation(projects.components.rootscreen.api)
    implementation(projects.components.deeplink.api)

    implementation(libs.compose.ui)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material)
    implementation(libs.compose.tooling)
}
