plugins {
    id("flipper.android-compose")
    id("flipper.anvil")
    id("kotlinx-serialization")
}

android.namespace = "com.flipperdevices.settings.impl"

dependencies {
    implementation(projects.components.settings.api)

    implementation(projects.components.core.di)
    implementation(projects.components.core.log)
    implementation(projects.components.core.preference)
    implementation(projects.components.core.ktx)
    implementation(projects.components.core.share)
    implementation(projects.components.core.activityholder)
    implementation(projects.components.analytics.shake2report.api)
    implementation(projects.components.analytics.metric.api)
    implementation(projects.components.core.ui.res)
    implementation(projects.components.core.ui.ktx)
    implementation(projects.components.core.ui.lifecycle)
    implementation(projects.components.core.ui.dialog)
    implementation(projects.components.core.ui.decompose)

    implementation(projects.components.core.ui.theme)

    implementation(projects.components.debug.api)
    implementation(projects.components.firstpair.api)
    implementation(projects.components.filemanager.api)
    implementation(projects.components.selfupdater.api)
    implementation(projects.components.nfc.mfkey32.api)
    implementation(projects.components.faphub.installation.all.api)
    implementation(projects.components.selfupdater.api)
    implementation(projects.components.notification.api)
    implementation(projects.components.inappnotification.api)

    implementation(projects.components.bridge.api)
    implementation(projects.components.bridge.dao.api)
    implementation(projects.components.bridge.pbutils)
    implementation(projects.components.bridge.service.api)
    implementation(projects.components.bridge.synchronization.api)
    implementation(libs.ble.common)

    implementation(libs.appcompat)

    // Compose
    implementation(libs.compose.ui)
    implementation(libs.compose.tooling)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material)
    implementation(libs.bundles.decompose)

    implementation(libs.lifecycle.compose)
    implementation(libs.lifecycle.viewmodel.ktx)

    implementation(libs.kotlin.serialization.json)
    implementation(libs.kotlin.immutable.collections)
}
