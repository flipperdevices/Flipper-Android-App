plugins {
    id("flipper.android-app")
    id("com.squareup.anvil")
    id("kotlin-kapt")
}

android {
    namespace = "com.flipperdevices.wearable"

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }
}

dependencies {
    implementation(projects.components.core.di)
    implementation(projects.components.core.preference)
    implementation(projects.components.core.activityholder)
    implementation(projects.components.core.ui.res)
    implementation(projects.components.core.ui.theme)
    implementation(projects.components.core.ui.navigation)

    implementation(projects.components.wearable.setup.api)
    implementation(projects.components.wearable.setup.impl)
    implementation(projects.components.wearable.emulate.wear.api)
    implementation(projects.components.wearable.emulate.wear.impl)
    implementation(projects.components.wearable.sync.wear.api)
    implementation(projects.components.wearable.sync.wear.impl)
    implementation(projects.components.wearable.theme)

    implementation(projects.components.keyparser.api)
    implementation(projects.components.keyparser.noop)

    implementation(projects.components.keyemulate.api)
    implementation(projects.components.keyemulate.impl)
    implementation(projects.components.bridge.service.api)
    implementation(projects.components.bridge.service.noop)
    implementation(projects.components.bridge.synchronization.api)
    implementation(projects.components.bridge.synchronization.stub)
    implementation(projects.components.bridge.dao.api)

    implementation(libs.appcompat)
    implementation(libs.timber)
    implementation(libs.datastore)

    implementation(projects.components.analytics.shake2report.api)
    releaseImplementation(projects.components.analytics.shake2report.noop)
    debugImplementation(projects.components.analytics.shake2report.impl)
    internalImplementation(projects.components.analytics.shake2report.impl)

    // Compose
    implementation(libs.compose.activity)
    implementation(libs.compose.ui)
    implementation(libs.compose.tooling)
    implementation(libs.compose.wear.foundation)
    implementation(libs.compose.wear.navigation)
    implementation(libs.compose.wear.material)

    implementation(libs.kotlin.immutable.collections)

    // Dagger deps
    implementation(libs.dagger)
    kapt(libs.dagger.kapt)

    implementation(libs.tangle.viewmodel.api)
    anvil(libs.tangle.viewmodel.compiler)
    implementation(libs.tangle.fragment.api)
    anvil(libs.tangle.fragment.compiler)
}
