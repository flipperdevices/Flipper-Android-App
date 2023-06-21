plugins {
    id("flipper.android-app")
    id("flipper.anvil")
    id("kotlin-kapt")
}

android.namespace = "com.flipperdevices.nfceditor.sample"
anvil.generateDaggerFactories.set(false) // AppComponent

android {
    buildFeatures.compose = true
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }
}

android {
    defaultConfig {
        applicationId = "com.flipperdevices.nfceditor.sample"
    }
}

dependencies {
    implementation(projects.components.core.preference)
    implementation(projects.components.core.ui.theme)
    implementation(projects.components.core.ui.lifecycle)
    implementation(projects.components.core.ui.navigation)
    implementation(projects.components.core.di)
    implementation(projects.components.core.log)

    implementation(projects.components.nfceditor.api)
    implementation(projects.components.nfceditor.impl)

    implementation(projects.components.keyparser.api)
    implementation(projects.components.keyparser.impl)

    implementation(projects.components.keyedit.api)
    implementation(projects.components.keyedit.noop)

    implementation(projects.components.analytics.metric.api)
    implementation(projects.components.analytics.metric.noop)

    implementation(projects.components.bridge.dao.api)
    implementation(projects.components.bridge.dao.impl)
    implementation(projects.components.bridge.synchronization.api)
    implementation(projects.components.bridge.synchronization.stub)

    implementation(projects.components.analytics.shake2report.api)
    releaseImplementation(projects.components.analytics.shake2report.noop)
    debugImplementation(projects.components.analytics.shake2report.impl)
    internalImplementation(projects.components.analytics.shake2report.impl)

    implementation(libs.appcompat)

    // Dagger deps
    implementation(libs.dagger)
    kapt(libs.dagger.kapt)
    implementation(libs.tangle.viewmodel.api)
    anvil(libs.tangle.viewmodel.compiler)
    implementation(libs.tangle.fragment.api)
    anvil(libs.tangle.fragment.compiler)

    implementation(libs.timber)

    // Compose
    implementation(libs.compose.activity)
    implementation(libs.compose.ui)
    implementation(libs.compose.tooling)
    implementation(libs.compose.navigation)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material)
}
