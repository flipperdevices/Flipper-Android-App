package com.flipperdevices.updater.screen.api

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.deeplink.api.DeepLinkHandler
import com.flipperdevices.deeplink.api.DispatcherPriority
import com.flipperdevices.deeplink.model.Deeplink
import com.flipperdevices.updater.api.UpdateDeeplinkHandler
import com.flipperdevices.updater.api.UpdaterUIApi
import com.flipperdevices.updater.model.FirmwareChannel
import com.flipperdevices.updater.model.FirmwareVersion
import com.flipperdevices.updater.model.UpdateRequest
import com.flipperdevices.updater.model.WebUpdaterFirmware
import com.github.terrakok.cicerone.Router
import com.squareup.anvil.annotations.ContributesBinding
import com.squareup.anvil.annotations.ContributesMultibinding
import javax.inject.Inject

@ContributesBinding(AppGraph::class)
@ContributesMultibinding(AppGraph::class, DeepLinkHandler::class)
class UpdateDeeplinkHandlerImpl @Inject constructor(
    private val updaterApi: UpdaterUIApi
) : UpdateDeeplinkHandler {
    override fun isSupportLink(link: Deeplink): DispatcherPriority? {
        return if (link is Deeplink.WebUpdate) DispatcherPriority.HIGH
        else null
    }

    override fun processLink(router: Router, link: Deeplink) {
        val webUpdate = link as Deeplink.WebUpdate
        val updateRequest = UpdateRequest(
            updateFrom = FirmwareVersion(
                channel = FirmwareChannel.CUSTOM,
                version = webUpdate.name
            ),
            updateTo = FirmwareVersion(
                channel = FirmwareChannel.CUSTOM,
                version = webUpdate.name
            ),
            content = WebUpdaterFirmware(webUpdate.url),
            changelog = null
        )
        updaterApi.openUpdateScreen(updateRequest)
    }
}
