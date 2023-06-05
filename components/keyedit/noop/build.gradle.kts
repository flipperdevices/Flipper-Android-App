plugins {
    id("flipper.android-lib")
    id("flipper.anvil")
}

android.namespace = "com.flipperdevices.keyedit.noop"

dependencies {
    implementation(projects.components.keyedit.api)

    implementation(projects.components.bridge.dao.api)
    implementation(projects.components.core.di)
    implementation(projects.components.core.ui.navigation)

    implementation(libs.compose.navigation)

    implementation(libs.appcompat)
}
