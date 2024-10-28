plugins {
    id("flipper.android-app")
    id("flipper.anvil.entrypoint")
}

android.namespace = "com.flipperdevices.nfceditor.sample"

android {
    defaultConfig {
        applicationId = "com.flipperdevices.nfceditor.sample"
    }
}

dependencies {
    implementation(projects.components.core.preference)
    implementation(projects.components.core.ui.theme)
    implementation(projects.components.core.ui.lifecycle)
    implementation(projects.components.core.ui.decompose)
    implementation(projects.components.core.di)
    implementation(projects.components.core.log)
    implementation(projects.components.core.storage)

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
    commonKsp(libs.dagger.compiler)

    implementation(libs.timber)
    implementation(libs.kotlin.immutable.collections)

    // Compose
    implementation(libs.compose.activity)
    implementation(libs.compose.ui)
    implementation(libs.compose.tooling)
    implementation(libs.bundles.decompose)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material)

    // Room
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
}
