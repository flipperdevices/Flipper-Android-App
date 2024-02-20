plugins {
    id("flipper.android-compose")
    id("flipper.anvil")
    id("kotlinx-serialization")
}

android.namespace = "com.flipperdevices.bottombar.impl"

dependencies {
    implementation(projects.components.bottombar.api)

    implementation(projects.components.core.di)
    implementation(projects.components.core.ktx)
    implementation(projects.components.core.log)
    implementation(projects.components.core.preference)
    implementation(projects.components.core.ui.res)
    implementation(projects.components.core.ui.ktx)
    implementation(projects.components.core.ui.theme)
    implementation(projects.components.core.ui.decompose)
    implementation(projects.components.core.ui.lifecycle)

    implementation(projects.components.info.api)
    implementation(projects.components.connection.api)
    implementation(projects.components.archive.api)
    implementation(projects.components.inappnotification.api)
    implementation(projects.components.hub.api)
    implementation(projects.components.deeplink.api)
    implementation(projects.components.unhandledexception.api)
    implementation(projects.components.notification.api)

    implementation(libs.kotlin.serialization.json)

    implementation(libs.appcompat)

    implementation(libs.compose.ui)
    implementation(libs.compose.material)
    implementation(libs.compose.tooling)
    implementation(libs.compose.foundation)
    implementation(libs.compose.pager)
    implementation(libs.bundles.decompose)
    implementation(libs.image.lottie)

    implementation(libs.kotlin.coroutines)
    implementation(libs.kotlin.immutable.collections)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
}
