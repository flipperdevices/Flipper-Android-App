package com.flipperdevices.nfc.mfkey32.screen.api

import android.content.Intent
import androidx.core.net.toUri
import androidx.navigation.NavController
import com.flipperdevices.bottombar.api.BottomNavigationHandleDeeplink
import com.flipperdevices.bottombar.model.BottomBarTab
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.deeplink.api.DeepLinkHandler
import com.flipperdevices.deeplink.api.DispatcherPriority
import com.flipperdevices.deeplink.model.Deeplink
import com.flipperdevices.nfc.mfkey32.api.MfKey32HandleDeeplink
import com.flipperdevices.nfc.mfkey32.api.MfKey32ScreenEntry
import com.squareup.anvil.annotations.ContributesMultibinding
import javax.inject.Inject

@ContributesMultibinding(AppGraph::class, DeepLinkHandler::class)
class DeepLinkMfKey32Handler @Inject constructor(
    private val bottomHandleDeeplink: BottomNavigationHandleDeeplink,
    private val mfKey32HandleDeeplink: MfKey32HandleDeeplink,
    private val mfKey32ScreenEntry: MfKey32ScreenEntry,
) : DeepLinkHandler {
    override fun isSupportLink(link: Deeplink): DispatcherPriority? {
        if (link is Deeplink.OpenMfKey) {
            return DispatcherPriority.HIGH
        }
        return null
    }

    override fun processLink(navController: NavController, link: Deeplink) {
        if (link !is Deeplink.OpenMfKey) return

        val intent = Intent().apply {
            data = mfKey32ScreenEntry.getMfKeyScreenByDeeplink().toUri()
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        bottomHandleDeeplink.onChangeTab(tab = BottomBarTab.HUB, force = true)
        mfKey32HandleDeeplink.handleDeepLink(intent)
    }
}
