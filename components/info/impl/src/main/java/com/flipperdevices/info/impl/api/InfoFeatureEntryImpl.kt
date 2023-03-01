package com.flipperdevices.info.impl.api

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
import javax.inject.Inject

@ContributesBinding(AppGraph::class, InfoFeatureEntry::class)
@ContributesMultibinding(AppGraph::class, AggregateFeatureEntry::class)
class InfoFeatureEntryImpl @Inject constructor(
    private val updaterCardApi: UpdaterCardApi,
    private val settingFeatureEntry: SettingsFeatureEntry,
    private val updaterFeatureEntry: UpdaterFeatureEntry
) : InfoFeatureEntry {

    private val deeplinkKey = DeeplinkConstants.KEY

    private fun start(): String = "@${ROUTE.name}/$deeplinkKey={$deeplinkKey}"

    override fun fullInfo(): String = "@${ROUTE.name}full"

    private val arguments = listOf(
        navArgument(DeeplinkConstants.KEY) {
            nullable = true
            type = DeeplinkNavType()
        }
    )

    private val deeplinkArguments = listOf(
        navDeepLink { uriPattern = Deeplink.buildDeeplinkPattern(DeeplinkConstants.WEB_UPDATE) }
    )

    override fun NavGraphBuilder.navigation(navController: NavHostController) {
        navigation(startDestination = start(), route = ROUTE.name) {
            composable(
                route = start(),
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
