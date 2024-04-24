plugins {
    id("flipper.android-app")
    id("flipper.anvil")
    id("kotlin-kapt")
    id("kotlinx-serialization")
}

android.namespace = "com.flipperdevices.bridge.connection"

android {
    buildFeatures.compose = true
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }
}

android {
    defaultConfig {
        applicationId = "com.flipperdevices.bridge.connection"
    }
}

dependencies {
    implementation(projects.components.core.ui.res)
    implementation(projects.components.core.ui.theme)
    implementation(projects.components.core.ui.lifecycle)
    implementation(projects.components.core.ui.decompose)
    implementation(projects.components.core.ui.ktx)
    implementation(projects.components.core.di)
    implementation(projects.components.core.log)
    implementation(projects.components.core.preference)

    implementation(projects.components.bridge.connection.transport.ble.api)
    implementation(projects.components.bridge.connection.transport.ble.impl)
    implementation(projects.components.bridge.connection.transport.common.api)
    implementation(projects.components.bridge.connection.transport.common.impl)
    implementation(projects.components.bridge.connection.orchestrator.api)
    implementation(projects.components.bridge.connection.orchestrator.impl)
    implementation(projects.components.bridge.connection.connectionbuilder.api)
    implementation(projects.components.bridge.connection.connectionbuilder.impl)
    implementation(projects.components.bridge.connection.config.api)
    implementation(projects.components.bridge.connection.config.impl)
    implementation(projects.components.bridge.connection.transportconfigbuilder.api)
    implementation(projects.components.bridge.connection.transportconfigbuilder.impl)
    implementation(projects.components.bridge.api)
    implementation(projects.components.bridge.pbutils)

    implementation(libs.appcompat)

    // Dagger deps
    implementation(libs.dagger)
    kapt(libs.dagger.kapt)

    implementation(libs.timber)

    implementation(libs.ble.kotlin.scanner)
    implementation(libs.ble.kotlin.client)

    implementation(libs.kotlin.immutable.collections)

    // Compose
    implementation(libs.compose.activity)
    implementation(libs.compose.ui)
    implementation(libs.compose.tooling)
    implementation(libs.bundles.decompose)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material)
}
