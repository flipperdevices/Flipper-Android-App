plugins {
    id("flipper.android-compose")
    id("com.squareup.anvil")
    id("kotlin-kapt")
}

dependencies {
    implementation(projects.components.nfceditor.api)

    implementation(projects.components.core.ui.lifecycle)
    implementation(projects.components.core.ui.fragment)
    implementation(projects.components.core.ui.theme)
    implementation(projects.components.core.ui.dialog)
    implementation(projects.components.core.ui.res)
    implementation(projects.components.core.ui.ktx)
    implementation(projects.components.core.ui.hexkeyboard)
    implementation(projects.components.core.ktx)
    implementation(projects.components.core.navigation)
    implementation(projects.components.core.log)
    implementation(projects.components.core.di)
    implementation(projects.components.core.preference)

    implementation(projects.components.bridge.dao.api)
    implementation(projects.components.bridge.synchronization.api)

    implementation(projects.components.keyedit.api)
    implementation(projects.components.keyscreen.shared)

    implementation(libs.cicerone)
    implementation(libs.appcompat)

    // Compose
    implementation(libs.compose.ui)
    implementation(libs.compose.tooling)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material)
    implementation(libs.compose.insets)

    implementation(libs.kotlin.immutable.collections)

    implementation(libs.lifecycle.compose)
    implementation(libs.ktx.fragment)

    // Dagger deps
    implementation(libs.dagger)
    kapt(libs.dagger.kapt)

    // Testing
    testImplementation(projects.components.core.test)
    testImplementation(libs.roboelectric)
    testImplementation(libs.junit)
    testImplementation(libs.ktx.testing)
    testImplementation(libs.mockito.kotlin)
    testImplementation(projects.components.bridge.dao.impl)
}
