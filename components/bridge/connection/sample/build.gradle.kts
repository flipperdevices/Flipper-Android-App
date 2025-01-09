import com.flipperdevices.buildlogic.ApkConfig.VERSION_NAME
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    id("flipper.android-app-multiplatform")
    id("com.google.devtools.ksp")
    id("flipper.anvil.entrypoint")
    id("kotlinx-serialization")
    id("flipper.multiplatform-dependencies")
}

android.namespace = "com.flipperdevices.bridge.connection"

android {
    buildFeatures.compose = true
    defaultConfig {
        applicationId = "com.flipperdevices.bridge.connection"
    }
}

compose.desktop {
    application {
        mainClass = "com.flipperdevices.bridge.connection.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "Flipper App"
            packageVersion = project.VERSION_NAME
        }
    }
}

commonDependencies {
    implementation(projects.components.core.di)
    implementation(projects.components.core.log)
    implementation(projects.components.core.preference)
    implementation(projects.components.core.storage)
    implementation(projects.components.core.share)
    implementation(projects.components.core.ktx)
    implementation(projects.components.core.ui.lifecycle)
    implementation(projects.components.core.ui.decompose)
    implementation(projects.components.core.ui.ktx)
    implementation(projects.components.core.ui.theme)

    implementation(libs.dagger)
    implementation(libs.anvil.utils.annotations)

    implementation(projects.components.bridge.connection.pbutils)
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
    implementation(projects.components.bridge.connection.device.common.api)
    implementation(projects.components.bridge.connection.device.fzero.api)
    implementation(projects.components.bridge.connection.device.fzero.impl)
    implementation(projects.components.bridge.connection.feature.common.api)
    implementation(projects.components.bridge.connection.feature.getinfo.api)
    implementation(projects.components.bridge.connection.feature.getinfo.impl)
    implementation(projects.components.bridge.connection.feature.lagsdetector.api)
    implementation(projects.components.bridge.connection.feature.lagsdetector.impl)
    implementation(projects.components.bridge.connection.feature.actionnotifier.api)
    implementation(projects.components.bridge.connection.feature.protocolversion.api)
    implementation(projects.components.bridge.connection.feature.protocolversion.impl)
    implementation(projects.components.bridge.connection.feature.provider.api)
    implementation(projects.components.bridge.connection.feature.provider.impl)
    implementation(projects.components.bridge.connection.feature.restartrpc.api)
    implementation(projects.components.bridge.connection.feature.restartrpc.impl)
    implementation(projects.components.bridge.connection.feature.rpc.api)
    implementation(projects.components.bridge.connection.feature.rpc.impl)
    implementation(projects.components.bridge.connection.feature.rpcinfo.api)
    implementation(projects.components.bridge.connection.feature.rpcinfo.impl)
    implementation(projects.components.bridge.connection.feature.rpcstats.api)
    implementation(projects.components.bridge.connection.feature.rpcstats.impl)
    implementation(projects.components.bridge.connection.feature.serialspeed.api)
    implementation(projects.components.bridge.connection.feature.serialspeed.impl)
    implementation(projects.components.bridge.connection.feature.storage.api)
    implementation(projects.components.bridge.connection.feature.storage.impl)
    implementation(projects.components.bridge.connection.feature.storageinfo.api)
    implementation(projects.components.bridge.connection.feature.storageinfo.impl)

    implementation(projects.components.filemngr.main.api)
    implementation(projects.components.filemngr.main.impl)
    implementation(projects.components.filemngr.listing.api)
    implementation(projects.components.filemngr.listing.impl)
    implementation(projects.components.filemngr.upload.api)
    implementation(projects.components.filemngr.upload.impl)
    implementation(projects.components.filemngr.search.api)
    implementation(projects.components.filemngr.search.impl)
    implementation(projects.components.filemngr.editor.api)
    implementation(projects.components.filemngr.editor.impl)
    implementation(projects.components.filemngr.download.api)
    implementation(projects.components.filemngr.download.impl)
    implementation(projects.components.filemngr.rename.api)
    implementation(projects.components.filemngr.rename.impl)
    implementation(projects.components.filemngr.create.api)
    implementation(projects.components.filemngr.create.impl)
    implementation(projects.components.filemngr.transfer.api)
    implementation(projects.components.filemngr.transfer.impl)

    implementation(projects.components.analytics.shake2report.api)
    implementation(projects.components.analytics.shake2report.noop)
    implementation(projects.components.analytics.metric.api)
    implementation(projects.components.analytics.metric.noop)

    implementation(libs.kotlin.immutable.collections)
}

desktopDependencies {
    implementation(projects.components.bridge.connection.transport.usb.api)
    implementation(projects.components.bridge.connection.transport.usb.impl)

    implementation(libs.decompose.jetpack)
}

dependencies {
    commonKsp(libs.dagger.compiler)
}

androidDependencies {
    implementation(projects.components.core.ui.res)

    implementation(projects.components.bridge.connection.transport.ble.api)
    implementation(projects.components.bridge.connection.transport.ble.impl)

    implementation(projects.components.bridge.api)

    implementation(projects.components.keyparser.api)
    implementation(projects.components.keyparser.impl)

    implementation(projects.components.deeplink.api)
    implementation(projects.components.deeplink.impl)

    implementation(libs.appcompat)

    implementation(libs.timber)

    implementation(libs.ble.kotlin.scanner)
    implementation(libs.ble.kotlin.client)

    implementation(libs.compose.activity)
}
