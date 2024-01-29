plugins {
    id("flipper.android-compose")
    id("flipper.anvil")
}

android.namespace = "com.flipperdevices.keyedit.noop"

dependencies {
    implementation(projects.components.keyedit.api)

    implementation(projects.components.bridge.dao.api)
    implementation(projects.components.core.di)
    implementation(projects.components.core.ui.decompose)

    implementation(libs.compose.ui)
    implementation(libs.decompose)

    implementation(libs.appcompat)
}
