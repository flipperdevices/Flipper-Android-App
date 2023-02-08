package com.flipperdevices.singleactivity.impl

import android.content.Context
import android.content.Intent
import com.flipperdevices.bottombar.api.BottomNavigationApi
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ktx.android.toFullString
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.flipperdevices.core.navigation.global.CiceroneGlobal
import com.flipperdevices.deeplink.api.DeepLinkDispatcher
import com.flipperdevices.deeplink.api.DeepLinkParser
import com.flipperdevices.deeplink.model.Deeplink
import com.flipperdevices.firstpair.api.FirstPairApi
import com.flipperdevices.updater.api.UpdaterApi
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Stack
import javax.inject.Inject
import javax.inject.Singleton

interface DeepLinkHelper {
    suspend fun onNewIntent(context: Context, intent: Intent)
    fun onNewDeeplink(deeplink: Deeplink)
    fun invalidate()
}

@Singleton
@Suppress("LongParameterList")
@ContributesBinding(AppGraph::class, DeepLinkHelper::class)
class DeepLinkHelperImpl @Inject constructor(
    private val firstPairApi: FirstPairApi,
    private val updaterApi: UpdaterApi,
    private val cicerone: CiceroneGlobal,
    private val bottomBarApi: BottomNavigationApi,
    private val deepLinkDispatcher: DeepLinkDispatcher,
    private val deepLinkParser: DeepLinkParser
) : DeepLinkHelper, LogTagProvider {
    override val TAG = "DeepLinkHelper"

    private val deeplinkStack = Stack<Deeplink>()

    override suspend fun onNewIntent(context: Context, intent: Intent) {
        info { "On new intent: ${intent.toFullString()}" }
        val deeplink = try {
            deepLinkParser.fromIntent(context, intent)
        } catch (throwable: Exception) {
            error(throwable) { "Failed parse deeplink" }
            null
        }

        withContext(Dispatchers.Main) {
            if (deeplink != null) {
                onNewDeeplink(deeplink)
            } else {
                invalidate()
            }
        }
    }

    override fun onNewDeeplink(deeplink: Deeplink) {
        info { "On new deeplink: $deeplink" }
        deeplinkStack.push(deeplink)
        invalidate()
    }

    override fun invalidate() {
        info { "Pending deeplinks size is ${deeplinkStack.size}" }

        if (firstPairApi.shouldWeOpenPairScreen()) {
            cicerone.getRouter().newRootScreen(firstPairApi.getFirstPairScreen())
            return
        }

        if (updaterApi.isUpdateInProcess()) {
            // TODO replace with UpdaterFeatureEntry
            return
        }

        if (deeplinkStack.empty()) {
            cicerone.getRouter().newRootScreen(bottomBarApi.getBottomNavigationFragment())
            return
        }

        val deeplink = deeplinkStack.pop()
        info { "Process deeplink $deeplink" }
        if (deeplink.isInternal) {
            val screen = bottomBarApi.getBottomNavigationFragment(deeplink)
            cicerone.getRouter().newRootScreen(screen)
        } else {
            cicerone.getRouter().newRootScreen(bottomBarApi.getBottomNavigationFragment())
            deepLinkDispatcher.process(cicerone.getRouter(), deeplink)
        }
    }
}
