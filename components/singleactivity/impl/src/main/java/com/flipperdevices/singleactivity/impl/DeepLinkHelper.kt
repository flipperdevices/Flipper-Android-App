package com.flipperdevices.singleactivity.impl

import android.content.Context
import android.content.Intent
import androidx.navigation.NavController
import androidx.navigation.navOptions
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ktx.android.toFullString
import com.flipperdevices.core.ktx.jre.withLock
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.flipperdevices.deeplink.api.DeepLinkDispatcher
import com.flipperdevices.deeplink.api.DeepLinkParser
import com.flipperdevices.deeplink.model.Deeplink
import com.flipperdevices.firstpair.api.FirstPairApi
import com.flipperdevices.updater.api.UpdaterApi
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
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

    private val mutex = Mutex()
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

        if (deeplink != null) {
            onNewDeeplink(navController, deeplink)
        } else {
            invalidate(navController)
        }
    }

    override suspend fun onNewDeeplink(navController: NavController, deeplink: Deeplink) {
        info { "On new deeplink: $deeplink" }
        deeplinkStack.push(deeplink)
        invalidate(navController)
    }

    override suspend fun invalidate(navController: NavController) = withLock(mutex, "invalidate") {
        info { "Pending deeplinks size is ${deeplinkStack.size}" }

        if (firstPairApi.shouldWeOpenPairScreen()) {
            info { "Open pair screen" }
            return@withLock
        }

        if (updaterApi.isUpdateInProcess()) {
            info { "Open updater" }
            openTopDestination(navController, updaterFeatureEntry.getUpdaterScreen())
            return@withLock
        }

        if (deeplinkStack.empty()) {
            info { "Open bottomBarFeatureEntry, because deeplinkStack is empty" }
            openTopDestination(navController, bottomBarFeatureEntry.start())
            return@withLock
        }

        val deeplink = deeplinkStack.pop()
        info { "Process deeplink $deeplink" }
        deepLinkDispatcher.process(navController = navController, deeplink = deeplink)
    }

    private suspend fun openTopDestination(
        navController: NavController,
        destination: String
    ) = withContext(Dispatchers.Main) {
        info { "Top destination is $destination" }
        val topScreenOptions = navOptions {
            popUpTo(0) {
                inclusive = true
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
        navController.navigate(destination, topScreenOptions)
    }

    override fun getStartDestination(): String {
        return when {
            firstPairApi.shouldWeOpenPairScreen() -> firstPairFeatureEntry.start()
            else -> bottomBarFeatureEntry.start()
        }
    }
}
