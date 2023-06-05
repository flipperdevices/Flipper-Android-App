import com.flipperdevices.buildlogic.ApkConfig.IS_GOOGLE_FEATURE_AVAILABLE
import com.flipperdevices.buildlogic.ApkConfig.IS_METRIC_ENABLED
import com.flipperdevices.buildlogic.ApkConfig.IS_SENTRY_ENABLED
import com.flipperdevices.buildlogic.ApkConfig.SOURCE_INSTALL
import com.flipperdevices.buildlogic.SourceInstall

plugins {
    id("flipper.android-app")
    id("com.squareup.anvil")
    id("kotlin-kapt")
}

android.namespace = "com.flipperdevices.app"

dependencies {
    implementation(projects.components.core.di)
    implementation(projects.components.core.ktx)
    implementation(projects.components.core.log)
    implementation(projects.components.core.preference)
    implementation(projects.components.core.activityholder)
    implementation(projects.components.core.ui.ktx)
    implementation(projects.components.core.ui.res)
    implementation(projects.components.core.ui.lifecycle)

    implementation(projects.components.core.ui.theme)
    implementation(projects.components.core.ui.navigation)

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
    implementation(projects.components.share.uploader)
    implementation(projects.components.share.cryptostorage)

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

    implementation(projects.components.keyemulate.api)
    implementation(projects.components.keyemulate.impl)

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
    implementation(projects.components.nfc.attack.api)
    implementation(projects.components.nfc.attack.impl)
    implementation(projects.components.nfc.tools.api)
    implementation(projects.components.nfc.tools.impl)

    implementation(projects.components.hub.api)
    implementation(projects.components.hub.impl)

    implementation(projects.components.widget.api)
    implementation(projects.components.widget.impl)
    implementation(projects.components.widget.screen)

    implementation(projects.components.faphub.appcard.api)
    implementation(projects.components.faphub.appcard.composable)

    implementation(projects.components.faphub.maincard.api)
    implementation(projects.components.faphub.maincard.impl)

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

    implementation(projects.components.faphub.installation.queue.api)
    implementation(projects.components.faphub.installation.queue.impl)

    implementation(projects.components.faphub.target.api)
    implementation(projects.components.faphub.target.impl)

    implementation(projects.components.faphub.installedtab.api)
    implementation(projects.components.faphub.installedtab.impl)

    implementation(projects.components.faphub.utils)

    implementation(projects.components.selfupdater.api)
    when (SOURCE_INSTALL) {
        SourceInstall.GOOGLE_PLAY -> {
            implementation(projects.components.selfupdater.googleplay)
        }

        SourceInstall.GITHUB -> {
            implementation(projects.components.selfupdater.thirdparty.api)
            implementation(projects.components.selfupdater.thirdparty.github)
        }
        SourceInstall.FDROID -> {
            implementation(projects.components.selfupdater.thirdparty.api)
            implementation(projects.components.selfupdater.thirdparty.fdroid)
        }
        else -> {
            implementation(projects.components.selfupdater.unknown)
        }
    }

    implementation(libs.ktor.client)

    implementation(libs.annotations)
    implementation(libs.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.work.ktx)
    implementation(libs.ktorfit.lib)

    implementation(libs.compose.coil.svg)
    implementation(libs.compose.coil)
    implementation(libs.compose.pager)

    implementation(libs.kotlin.coroutines)
    implementation(libs.ktx.activity)

    implementation(libs.dagger)
    kapt(libs.dagger.kapt)
    implementation(libs.tangle.viewmodel.api)
    anvil(libs.tangle.viewmodel.compiler)
    implementation(libs.tangle.fragment.api)
    anvil(libs.tangle.fragment.compiler)

    implementation(libs.timber)
}
