plugins {
    id("flipper.multiplatform-compose")
    id("flipper.multiplatform-dependencies")
    id("com.google.devtools.ksp")
    id("flipper.anvil")
    id("kotlinx-serialization")
}

android.namespace = "com.flipperdevices.bridge.connection.sample.shared"

commonDependencies {
    api(projects.components.core.di)
    api(projects.components.core.log)
    api(projects.components.core.preference)
    api(projects.components.core.storage)
    api(projects.components.core.share)
    api(projects.components.core.ktx)
    api(projects.components.core.ui.lifecycle)
    api(projects.components.core.ui.decompose)
    api(projects.components.core.ui.ktx)
    api(projects.components.core.ui.theme)

    api(libs.dagger)
    api(libs.anvil.utils.annotations)

    api(projects.components.bridge.connection.pbutils)
    api(projects.components.bridge.connection.transport.common.api)
    api(projects.components.bridge.connection.transport.common.impl)
    api(projects.components.bridge.connection.orchestrator.api)
    api(projects.components.bridge.connection.orchestrator.impl)
    api(projects.components.bridge.connection.connectionbuilder.api)
    api(projects.components.bridge.connection.connectionbuilder.impl)
    api(projects.components.bridge.connection.config.api)
    api(projects.components.bridge.connection.config.impl)
    api(projects.components.bridge.connection.transportconfigbuilder.api)
    api(projects.components.bridge.connection.transportconfigbuilder.impl)
    api(projects.components.bridge.connection.device.common.api)
    api(projects.components.bridge.connection.device.fzero.api)
    api(projects.components.bridge.connection.device.fzero.impl)

    api(projects.components.bridge.connection.feature.common.api)
    api(projects.components.bridge.connection.feature.getinfo.api)
    api(projects.components.bridge.connection.feature.getinfo.impl)
    api(projects.components.bridge.connection.feature.lagsdetector.api)
    api(projects.components.bridge.connection.feature.lagsdetector.impl)
    api(projects.components.bridge.connection.feature.actionnotifier.api)
    api(projects.components.bridge.connection.feature.protocolversion.api)
    api(projects.components.bridge.connection.feature.protocolversion.impl)
    api(projects.components.bridge.connection.feature.provider.api)
    api(projects.components.bridge.connection.feature.provider.impl)
    api(projects.components.bridge.connection.feature.restartrpc.api)
    api(projects.components.bridge.connection.feature.restartrpc.impl)
    api(projects.components.bridge.connection.feature.rpc.api)
    api(projects.components.bridge.connection.feature.rpc.impl)
    api(projects.components.bridge.connection.feature.rpcinfo.api)
    api(projects.components.bridge.connection.feature.rpcinfo.impl)
    api(projects.components.bridge.connection.feature.rpcstats.api)
    api(projects.components.bridge.connection.feature.rpcstats.impl)
    api(projects.components.bridge.connection.feature.serialspeed.api)
    api(projects.components.bridge.connection.feature.serialspeed.impl)
    api(projects.components.bridge.connection.feature.storage.api)
    api(projects.components.bridge.connection.feature.storage.impl)
    api(projects.components.bridge.connection.feature.storageinfo.api)
    api(projects.components.bridge.connection.feature.storageinfo.impl)
    api(projects.components.bridge.connection.feature.alarm.api)
    api(projects.components.bridge.connection.feature.alarm.impl)
    api(projects.components.bridge.connection.feature.deviceColor.api)
    api(projects.components.bridge.connection.feature.deviceColor.impl)
    api(projects.components.bridge.connection.feature.appstart.api)
    api(projects.components.bridge.connection.feature.appstart.impl)
    api(projects.components.bridge.connection.feature.screenstreaming.api)
    api(projects.components.bridge.connection.feature.screenstreaming.impl)
    api(projects.components.bridge.connection.feature.update.api)
    api(projects.components.bridge.connection.feature.update.impl)
    api(projects.components.bridge.connection.feature.emulate.api)
    api(projects.components.bridge.connection.feature.emulate.impl)

    api(projects.components.filemngr.main.api)
    api(projects.components.filemngr.main.impl)
    api(projects.components.filemngr.listing.api)
    api(projects.components.filemngr.listing.impl)
    api(projects.components.filemngr.upload.api)
    api(projects.components.filemngr.upload.impl)
    api(projects.components.filemngr.search.api)
    api(projects.components.filemngr.search.impl)
    api(projects.components.filemngr.editor.api)
    api(projects.components.filemngr.editor.impl)
    api(projects.components.filemngr.download.api)
    api(projects.components.filemngr.download.impl)
    api(projects.components.filemngr.rename.api)
    api(projects.components.filemngr.rename.impl)
    api(projects.components.filemngr.create.api)
    api(projects.components.filemngr.create.impl)
    api(projects.components.filemngr.transfer.api)
    api(projects.components.filemngr.transfer.impl)

    api(projects.components.analytics.shake2report.api)
    api(projects.components.analytics.shake2report.noop)
    api(projects.components.analytics.metric.api)
    api(projects.components.analytics.metric.noop)

    api(libs.kotlin.immutable.collections)

    implementation(projects.components.core.ui.res)
}

desktopDependencies {
    api(projects.components.bridge.connection.transport.usb.api)
    api(projects.components.bridge.connection.transport.usb.impl)

    api(libs.decompose.jetpack)

    implementation(libs.jserial)
}

androidDependencies {
    api(projects.components.core.ui.res)

    api(projects.components.bridge.connection.transport.ble.api)
    api(projects.components.bridge.connection.transport.ble.impl)

    api(projects.components.bridge.connection.transport.usb.api)
    api(projects.components.bridge.connection.transport.usb.impl)

    api(projects.components.firstpair.connection.api)

    api(projects.components.keyparser.api)
    api(projects.components.keyparser.impl)

    api(projects.components.deeplink.api)
    api(projects.components.deeplink.impl)

    api(libs.appcompat)

    api(libs.timber)

    api(libs.ble.kotlin.scanner)
    api(libs.ble.kotlin.client)

    api(libs.compose.activity)
    implementation(libs.usb.android)
}
