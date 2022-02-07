plugins {
    androidApplication
    id("com.squareup.anvil")
    kotlin("kapt")
}

dependencies {
    implementation(projects.components.core.di)
    implementation(projects.components.core.ktx)
    implementation(projects.components.core.log)
    implementation(projects.components.core.navigation)
    implementation(projects.components.core.preference)
    implementation(projects.components.core.ui)

    implementation(projects.components.firstpair.api)
    implementation(projects.components.firstpair.impl)

    implementation(projects.components.info.api)
    implementation(projects.components.info.impl)

    implementation(projects.components.bottombar.api)
    implementation(projects.components.bottombar.impl)

    implementation(projects.components.filemanager.api)
    implementation(projects.components.filemanager.impl)

    implementation(projects.components.screenstreaming.api)
    implementation(projects.components.screenstreaming.impl)

    implementation(projects.components.share.api)
    implementation(projects.components.share.receive)
    implementation(projects.components.share.export)

    implementation(projects.components.singleactivity.api)
    implementation(projects.components.singleactivity.impl)

    implementation(projects.components.deeplink.api)
    implementation(projects.components.deeplink.impl)

    implementation(projects.components.debug.api)
    implementation(projects.components.debug.impl)

    implementation(projects.components.archive.api)
    implementation(projects.components.archive.impl)

    implementation(projects.components.connection.api)
    implementation(projects.components.connection.impl)

    implementation(projects.components.keyscreen.api)
    implementation(projects.components.keyscreen.impl)

    implementation(projects.components.bridge.dao.api)
    implementation(projects.components.bridge.dao.impl)
    implementation(projects.components.bridge.synchronization.api)
    implementation(projects.components.bridge.synchronization.impl)
    implementation(projects.components.bridge.service.api)
    implementation(projects.components.bridge.service.impl)
    implementation(projects.components.bridge.api)
    implementation(projects.components.bridge.impl)
    implementation(libs.ble.common)
    implementation(libs.ble.scan)
    implementation(libs.ble.ktx)

    implementation(projects.components.analytics.shake2report.api)
    releaseImplementation(projects.components.analytics.shake2report.noop)
    debugImplementation(projects.components.analytics.shake2report.impl)
    internalImplementation(projects.components.analytics.shake2report.impl)

    implementation(libs.annotations)
    implementation(libs.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)

    implementation(libs.kotlin.coroutines)
    implementation(libs.ktx.activity)

    implementation(libs.dagger)
    kapt(libs.dagger.kapt)

    implementation(libs.cicerone)
    implementation(libs.timber)
}
