package com.flipperdevices.info.impl.api

import android.net.Uri
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import androidx.navigation.navigation
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ui.navigation.AggregateFeatureEntry
import com.flipperdevices.core.ui.navigation.LocalGlobalNavigationNavStack
import com.flipperdevices.deeplink.model.Deeplink
import com.flipperdevices.deeplink.model.DeeplinkConstants
import com.flipperdevices.deeplink.model.DeeplinkNavType
import com.flipperdevices.info.api.screen.InfoFeatureEntry
import com.flipperdevices.info.impl.compose.screens.ComposableDeviceInfoScreen
import com.flipperdevices.info.impl.compose.screens.ComposableFullDeviceInfoScreen
import com.flipperdevices.settings.api.SettingsFeatureEntry
import com.flipperdevices.updater.api.UpdaterCardApi
import com.flipperdevices.updater.api.UpdaterFeatureEntry
import com.squareup.anvil.annotations.ContributesBinding
import com.squareup.anvil.annotations.ContributesMultibinding
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

private const val DEEPLINK_KEY = DeeplinkConstants.KEY
private const val DEEPLINK_SCHEME = DeeplinkConstants.SCHEMA
private const val DEEPLINK_WEB_UPDATER_URL = "${DEEPLINK_SCHEME}web_updater={$DEEPLINK_KEY}"

@ContributesBinding(AppGraph::class, InfoFeatureEntry::class)
@ContributesMultibinding(AppGraph::class, AggregateFeatureEntry::class)
class InfoFeatureEntryImpl @Inject constructor(
    private val updaterCardApi: UpdaterCardApi,
    private val settingFeatureEntry: SettingsFeatureEntry,
    private val updaterFeatureEntry: UpdaterFeatureEntry
) : InfoFeatureEntry {

    private val infoRoute = "@${ROUTE.name}/$DEEPLINK_KEY={$DEEPLINK_KEY}"

    override fun fullInfo(): String = "@${ROUTE.name}full"
    override fun getWebUpdateByDeeplink(deeplink: Deeplink): String {
        val deeplinkStr = Uri.encode(Json.encodeToString(deeplink))
        return "${DEEPLINK_SCHEME}web_updater=$deeplinkStr"
    }

    private val arguments = listOf(
        navArgument(DeeplinkConstants.KEY) {
            nullable = true
            type = DeeplinkNavType()
        }
    )

    private val deeplinkArguments = listOf(
        navDeepLink { uriPattern = DEEPLINK_WEB_UPDATER_URL }
    )

    override fun NavGraphBuilder.navigation(navController: NavHostController) {
        navigation(startDestination = infoRoute, route = ROUTE.name) {
            composable(
                route = infoRoute,
                arguments = arguments,
                deepLinks = deeplinkArguments
            ) {
                val globalNavController = LocalGlobalNavigationNavStack.current
                ComposableDeviceInfoScreen(
                    updaterCardApi,
                    onOpenFullDeviceInfo = { navController.navigate(fullInfo()) },
                    onOpenOptions = { navController.navigate(settingFeatureEntry.ROUTE.name) },
                    onStartUpdateRequest = {
                        globalNavController.navigate(updaterFeatureEntry.getUpdaterScreen(it))
                    }
                )
            }
            composable("@${ROUTE.name}full") {
                ComposableFullDeviceInfoScreen(navController)
            }
        }
    }
}
