package com.flipperdevices.bottombar.impl.navigate

import com.flipperdevices.archive.api.ArchiveApi
import com.flipperdevices.bottombar.impl.model.FlipperBottomTab
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.deeplink.model.Deeplink
import com.flipperdevices.hub.api.HubApi
import com.flipperdevices.info.api.screen.InfoScreenProvider
import com.github.terrakok.cicerone.Screen
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

@ContributesBinding(AppGraph::class)
class ScreenTabProviderImpl @Inject constructor(
    private val infoScreenProvider: InfoScreenProvider,
    private val archiveApi: ArchiveApi,
    private val hubApi: HubApi
) : ScreenTabProvider {
    override fun getScreen(tab: FlipperBottomTab, deeplink: Deeplink?): Screen {
        return when (tab) {
            FlipperBottomTab.DEVICE -> infoScreenProvider.deviceInformationScreen(deeplink)
            FlipperBottomTab.ARCHIVE -> archiveApi.getArchiveScreen()
            FlipperBottomTab.HUB -> hubApi.getHubScreen()
        }
    }
}
