plugins {
    id("flipper.android-app")
    id("com.squareup.anvil")
    id("kotlin-kapt")
}

android.buildFeatures.viewBinding = true

android {
    defaultConfig {
        applicationId = "com.flipperdevices.nfceditor.sample"
    }
}

dependencies {
    implementation(projects.components.core.ui.fragment)
    implementation(projects.components.core.ui.theme)
    implementation(projects.components.core.ui.lifecycle)
    implementation(projects.components.core.di)
    implementation(projects.components.core.log)
    implementation(projects.components.core.navigation)
    implementation(projects.components.core.preference)

    implementation(projects.components.nfceditor.api)
    implementation(projects.components.nfceditor.impl)

    implementation(projects.components.keyedit.api)
    implementation(projects.components.keyedit.noop)

    implementation(projects.components.bridge.dao.api)
    implementation(projects.components.bridge.dao.impl)
    implementation(projects.components.bridge.synchronization.api)
    implementation(projects.components.bridge.synchronization.stub)

    implementation(projects.components.analytics.shake2report.api)
    releaseImplementation(projects.components.analytics.shake2report.noop)
    debugImplementation(projects.components.analytics.shake2report.impl)
    internalImplementation(projects.components.analytics.shake2report.impl)

    implementation(libs.appcompat)

    implementation(libs.dagger)
    kapt(libs.dagger.kapt)

    implementation(libs.cicerone)
    implementation(libs.timber)
}
