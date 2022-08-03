plugins {
    id("androidApplication")
    id("com.squareup.anvil")
    kotlin("kapt")
}

dependencies {
    implementation(projects.components.core.di)
    implementation(projects.components.core.ktx)
    implementation(projects.components.core.log)
    implementation(projects.components.core.navigation)
    implementation(projects.components.core.preference)
    implementation(projects.components.core.activityholder)
    implementation(projects.components.core.ui.ktx)
    implementation(projects.components.core.ui.res)
    implementation(projects.components.core.ui.lifecycle)
    implementation(projects.components.core.ui.fragment)
    implementation(projects.components.core.ui.theme)

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

    implementation(projects.components.singleactivity.api)
    implementation(projects.components.singleactivity.impl)

    implementation(projects.components.deeplink.api)
    implementation(projects.components.deeplink.impl)

    implementation(projects.components.debug.api)
    implementation(projects.components.debug.stresstest)

    implementation(projects.components.archive.api)
    implementation(projects.components.archive.impl)
    implementation(projects.components.archive.category)
    implementation(projects.components.archive.search)

    implementation(projects.components.connection.api)
    implementation(projects.components.connection.impl)

    implementation(projects.components.keyscreen.api)
    implementation(projects.components.keyscreen.impl)
    implementation(projects.components.keyscreen.shared)

    implementation(projects.components.keyedit.api)
    implementation(projects.components.keyedit.impl)

    implementation(projects.components.inappnotification.api)
    implementation(projects.components.inappnotification.impl)

    implementation(projects.components.settings.api)
    implementation(projects.components.settings.impl)

    implementation(projects.components.updater.api)
    implementation(projects.components.updater.impl)
    implementation(projects.components.updater.downloader)
    implementation(projects.components.updater.screen)
    implementation(projects.components.updater.card)

    implementation(projects.components.bridge.dao.api)
    implementation(projects.components.bridge.dao.impl)
    implementation(projects.components.bridge.synchronization.api)
    implementation(projects.components.bridge.synchronization.impl)
    implementation(projects.components.bridge.synchronization.ui)
    implementation(projects.components.bridge.service.api)
    implementation(projects.components.bridge.service.impl)
    implementation(projects.components.bridge.api)
    implementation(projects.components.bridge.impl)
    implementation(libs.ble.common)
    implementation(libs.ble.scan)

    implementation(projects.components.analytics.shake2report.api)
    releaseImplementation(projects.components.analytics.shake2report.noop)
    debugImplementation(projects.components.analytics.shake2report.impl)
    internalImplementation(projects.components.analytics.shake2report.impl)

    implementation(projects.components.analytics.metric.api)
    implementation(projects.components.analytics.metric.impl)

    implementation(projects.components.nfceditor.api)
    implementation(projects.components.nfceditor.impl)

    implementation(libs.ktor.client)

    implementation(libs.annotations)
    implementation(libs.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)

    implementation(libs.kotlin.coroutines)
    implementation(libs.ktx.activity)

    implementation(libs.dagger)
    kapt(libs.dagger.kapt)
    implementation(libs.tangle.viewmodel.api)
    anvil(libs.tangle.viewmodel.compiler)
    implementation(libs.tangle.fragment.api)
    anvil(libs.tangle.fragment.compiler)

    implementation(libs.cicerone)
    implementation(libs.timber)

    implementation(libs.install.referrer)
}
