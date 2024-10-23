import com.flipperdevices.buildlogic.ApkConfig.IS_GOOGLE_FEATURE_AVAILABLE
import com.flipperdevices.buildlogic.ApkConfig.IS_METRIC_ENABLED
import com.flipperdevices.buildlogic.ApkConfig.IS_SENTRY_ENABLED
import com.flipperdevices.buildlogic.ApkConfig.SOURCE_INSTALL
import com.flipperdevices.buildlogic.SourceInstall

plugins {
    id("flipper.android-app")
    id("flipper.anvil.entrypoint")
    alias(libs.plugins.google.gms)
    alias(libs.plugins.baselineprofile)
}

android.namespace = "com.flipperdevices.app"

dependencies {
    implementation(projects.components.core.di)
    implementation(projects.components.core.ktx)
    implementation(projects.components.core.log)
    implementation(projects.components.core.preference)
    implementation(projects.components.core.storage)
    implementation(projects.components.core.activityholder)
    implementation(projects.components.core.permission.api)
    implementation(projects.components.core.permission.impl)
    implementation(projects.components.core.ui.ktx)
    implementation(projects.components.core.ui.res)
    implementation(projects.components.core.ui.lifecycle)
    implementation(projects.components.core.ui.scrollbar)

    implementation(projects.components.core.ui.theme)
    implementation(projects.components.core.ui.decompose)

    implementation(projects.components.firstpair.api)
    implementation(projects.components.firstpair.impl)

    implementation(projects.components.info.api)
    implementation(projects.components.info.impl)

    implementation(projects.components.bottombar.api)
    implementation(projects.components.bottombar.impl)

    implementation(projects.components.filemanager.api)
    implementation(projects.components.filemanager.impl)

    implementation(projects.components.remoteControls.apiBackend)
    implementation(projects.components.remoteControls.apiBackendFlipper)
    implementation(projects.components.remoteControls.coreModel)
    implementation(projects.components.remoteControls.coreUi)
    implementation(projects.components.remoteControls.brands.api)
    implementation(projects.components.remoteControls.brands.impl)
    implementation(projects.components.remoteControls.categories.api)
    implementation(projects.components.remoteControls.categories.impl)
    implementation(projects.components.remoteControls.grid.remote.api)
    implementation(projects.components.remoteControls.grid.remote.impl)
    implementation(projects.components.remoteControls.grid.saved.api)
    implementation(projects.components.remoteControls.grid.saved.impl)
    implementation(projects.components.remoteControls.main.api)
    implementation(projects.components.remoteControls.main.impl)
    implementation(projects.components.remoteControls.setup.api)
    implementation(projects.components.remoteControls.setup.impl)

    implementation(projects.components.screenstreaming.api)
    implementation(projects.components.screenstreaming.impl)

    implementation(projects.components.share.api)
    implementation(projects.components.share.receive)
    implementation(projects.components.share.uploader)
    implementation(projects.components.share.cryptostorage)

    implementation(projects.components.singleactivity.api)
    implementation(projects.components.singleactivity.impl)

    implementation(projects.components.rootscreen.api)
    implementation(projects.components.rootscreen.impl)

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

    implementation(projects.components.keyemulate.api)
    implementation(projects.components.keyemulate.impl)

    implementation(projects.components.keyparser.api)
    implementation(projects.components.keyparser.impl)

    implementation(projects.components.infrared.api)
    implementation(projects.components.infrared.impl)
    implementation(projects.components.infrared.editor)

    implementation(projects.components.inappnotification.api)
    implementation(projects.components.inappnotification.impl)

    implementation(projects.components.settings.api)
    implementation(projects.components.settings.impl)

    implementation(projects.components.updater.api)
    implementation(projects.components.updater.impl)
    implementation(projects.components.updater.downloader)
    implementation(projects.components.updater.screen)
    implementation(projects.components.updater.card)
    implementation(projects.components.updater.subghz)

    implementation(projects.components.changelog.api)
    implementation(projects.components.changelog.impl)

    implementation(projects.components.bridge.dao.api)
    implementation(projects.components.bridge.dao.impl)
    implementation(projects.components.bridge.rpc.api)
    implementation(projects.components.bridge.rpc.impl)
    implementation(projects.components.bridge.synchronization.api)
    implementation(projects.components.bridge.synchronization.impl)
    implementation(projects.components.bridge.synchronization.ui)
    implementation(projects.components.bridge.service.api)
    implementation(projects.components.bridge.service.impl)
    implementation(projects.components.bridge.rpcinfo.api)
    implementation(projects.components.bridge.rpcinfo.impl)
    implementation(projects.components.bridge.api)
    implementation(projects.components.bridge.impl)
    implementation(libs.ble.common)
    implementation(libs.ble.scan)

    implementation(projects.components.analytics.shake2report.api)
    if (IS_SENTRY_ENABLED) {
        implementation(projects.components.analytics.shake2report.impl)
    } else {
        implementation(projects.components.analytics.shake2report.noop)
    }

    implementation(projects.components.analytics.metric.api)
    if (IS_METRIC_ENABLED) {
        implementation(projects.components.analytics.metric.impl)
    } else {
        implementation(projects.components.analytics.metric.noop)
    }

    implementation(projects.components.nfceditor.api)
    implementation(projects.components.nfceditor.impl)

    implementation(projects.components.wearable.sync.handheld.api)
    if (IS_GOOGLE_FEATURE_AVAILABLE) {
        implementation(libs.wear.gms)
        implementation(projects.components.wearable.sync.handheld.impl)
        implementation(projects.components.wearable.emulate.handheld.impl)
    } else {
        implementation(projects.components.wearable.sync.handheld.noop)
    }

    implementation(projects.components.nfc.mfkey32.api)
    implementation(projects.components.nfc.mfkey32.screen)
    implementation(projects.components.nfc.tools.api)
    implementation(projects.components.nfc.tools.impl)

    implementation(projects.components.toolstab.api)
    implementation(projects.components.toolstab.impl)

    implementation(projects.components.widget.api)
    implementation(projects.components.widget.impl)
    implementation(projects.components.widget.screen)

    implementation(projects.components.faphub.appcard.api)
    implementation(projects.components.faphub.appcard.composable)

    implementation(projects.components.faphub.dao.api)
    implementation(projects.components.faphub.dao.network)

    implementation(projects.components.faphub.main.api)
    implementation(projects.components.faphub.main.impl)

    implementation(projects.components.faphub.catalogtab.api)
    implementation(projects.components.faphub.catalogtab.impl)

    implementation(projects.components.faphub.search.api)
    implementation(projects.components.faphub.search.impl)

    implementation(projects.components.faphub.category.api)
    implementation(projects.components.faphub.category.impl)

    implementation(projects.components.faphub.fapscreen.api)
    implementation(projects.components.faphub.fapscreen.impl)

    implementation(projects.components.faphub.installation.button.api)
    implementation(projects.components.faphub.installation.button.impl)

    implementation(projects.components.faphub.installation.manifest.api)
    implementation(projects.components.faphub.installation.manifest.impl)

    implementation(projects.components.faphub.installation.stateprovider.api)
    implementation(projects.components.faphub.installation.stateprovider.impl)

    implementation(projects.components.faphub.installation.all.api)
    implementation(projects.components.faphub.installation.all.impl)

    implementation(projects.components.faphub.installation.queue.api)
    implementation(projects.components.faphub.installation.queue.impl)

    implementation(projects.components.faphub.target.api)
    implementation(projects.components.faphub.target.impl)

    implementation(projects.components.faphub.report.api)
    implementation(projects.components.faphub.report.impl)

    implementation(projects.components.faphub.installedtab.api)
    implementation(projects.components.faphub.installedtab.impl)

    implementation(projects.components.faphub.uninstallbutton.api)
    implementation(projects.components.faphub.uninstallbutton.impl)

    implementation(projects.components.faphub.errors.api)
    implementation(projects.components.faphub.errors.impl)

    implementation(projects.components.faphub.screenshotspreview.api)
    implementation(projects.components.faphub.screenshotspreview.impl)

    implementation(projects.components.faphub.utils)

    implementation(projects.components.selfupdater.api)
    implementation(projects.components.selfupdater.impl)
    when (SOURCE_INSTALL) {
        SourceInstall.GOOGLE_PLAY ->
            implementation(projects.components.selfupdater.googleplay)

        SourceInstall.GITHUB -> {
            implementation(projects.components.selfupdater.thirdparty.api)
            implementation(projects.components.selfupdater.thirdparty.github)
        }

        SourceInstall.DEBUG ->
            implementation(projects.components.selfupdater.debug)

        else ->
            implementation(projects.components.selfupdater.unknown)
    }

    implementation(projects.components.unhandledexception.api)
    implementation(projects.components.unhandledexception.impl)

    implementation(projects.components.notification.api)
    if (IS_GOOGLE_FEATURE_AVAILABLE) {
        implementation(projects.components.notification.impl)
    } else {
        implementation(projects.components.notification.noop)
    }

    implementation(libs.ktor.client)

    implementation(libs.annotations)
    implementation(libs.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.work.ktx)

    implementation(libs.bundles.decompose)

    implementation(libs.coil.svg)
    implementation(libs.coil.compose)
    implementation(libs.compose.pager)

    implementation(libs.kotlin.coroutines)
    implementation(libs.ktx.activity)

    implementation(libs.dagger)
    commonKsp(libs.dagger.compiler)

    implementation(libs.timber)

    implementation(libs.profileinstaller)
    baselineProfile(projects.instances.android.baselineprofile)

    // Room
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
}

baselineProfile {
    // Generate a single profile for each variants
    mergeIntoMain = false
    automaticGenerationDuringBuild = false
    saveInSrc = true
}
