package com.flipperdevices.singleactivity.impl

import android.content.Context
import android.content.Intent
import androidx.navigation.NavController
import androidx.navigation.navOptions
import com.flipperdevices.bottombar.api.BottomNavigationFeatureEntry
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ktx.android.toFullString
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.flipperdevices.deeplink.api.DeepLinkDispatcher
import com.flipperdevices.deeplink.api.DeepLinkParser
import com.flipperdevices.deeplink.model.Deeplink
import com.flipperdevices.firstpair.api.FirstPairApi
import com.flipperdevices.firstpair.api.FirstPairFeatureEntry
import com.flipperdevices.updater.api.UpdaterApi
import com.flipperdevices.updater.api.UpdaterFeatureEntry
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Stack
import javax.inject.Inject
import javax.inject.Singleton

interface DeepLinkHelper {
    suspend fun onNewIntent(context: Context, navController: NavController, intent: Intent)
    suspend fun onNewDeeplink(navController: NavController, deeplink: Deeplink)
    suspend fun invalidate(navController: NavController)
    fun getStartDestination(): String
}

@Singleton
@Suppress("LongParameterList")
@ContributesBinding(AppGraph::class, DeepLinkHelper::class)
class DeepLinkHelperImpl @Inject constructor(
    private val firstPairApi: FirstPairApi,
    private val firstPairFeatureEntry: FirstPairFeatureEntry,
    private val updaterApi: UpdaterApi,
    private val updaterFeatureEntry: UpdaterFeatureEntry,
    private val bottomBarFeatureEntry: BottomNavigationFeatureEntry,
    private val deepLinkParser: DeepLinkParser,
    private val deepLinkDispatcher: DeepLinkDispatcher
) : DeepLinkHelper, LogTagProvider {
    override val TAG = "DeepLinkHelper"

    private val deeplinkStack = Stack<Deeplink>()

    override suspend fun onNewIntent(
        context: Context,
        navController: NavController,
        intent: Intent
    ) {
        info { "On new intent: ${intent.toFullString()}" }
        val deeplink = try {
            deepLinkParser.fromIntent(context, intent)
        } catch (throwable: Exception) {
            error(throwable) { "Failed parse deeplink" }
            null
        }

        withContext(Dispatchers.Main) {
            if (deeplink != null) {
                onNewDeeplink(navController, deeplink)
            } else {
                invalidate(navController)
            }
        }
    }

    override suspend fun onNewDeeplink(navController: NavController, deeplink: Deeplink) {
        info { "On new deeplink: $deeplink" }
        deeplinkStack.push(deeplink)
        invalidate(navController)
    }

    override suspend fun invalidate(navController: NavController) = withContext(Dispatchers.Main) {
        info { "Pending deeplinks size is ${deeplinkStack.size}" }

        val topScreenOptions = navOptions {
            popUpTo(0) {
                inclusive = true
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }

        if (firstPairApi.shouldWeOpenPairScreen()) {
            return@withContext
        }

        if (updaterApi.isUpdateInProcess()) {
            navController.navigate(updaterFeatureEntry.getUpdaterScreen(), topScreenOptions)
            return@withContext
        }

        if (deeplinkStack.empty()) {
            navController.navigate(bottomBarFeatureEntry.start(), topScreenOptions)
            return@withContext
        }

        val deeplink = deeplinkStack.pop()
        info { "Process deeplink $deeplink" }
        deepLinkDispatcher.process(navController = navController, deeplink = deeplink)
    }

    override fun getStartDestination(): String {
        return when {
            firstPairApi.shouldWeOpenPairScreen() -> firstPairFeatureEntry.start()
            else -> bottomBarFeatureEntry.start()
        }
    }
}
