package com.flipperdevices.info.impl.api

import android.content.Intent
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import androidx.navigation.navigation
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ui.navigation.AggregateFeatureEntry
import com.flipperdevices.deeplink.model.DeeplinkConstants
import com.flipperdevices.info.impl.compose.screens.ComposableDeviceInfoScreen
import com.flipperdevices.info.impl.compose.screens.ComposableFullDeviceInfoScreen
import com.flipperdevices.settings.api.SettingsFeatureEntry
import com.flipperdevices.updater.api.UpdaterCardApi
import com.squareup.anvil.annotations.ContributesBinding
import com.squareup.anvil.annotations.ContributesMultibinding
import javax.inject.Inject

@ContributesBinding(AppGraph::class, InfoFeatureEntry::class)
@ContributesMultibinding(AppGraph::class, AggregateFeatureEntry::class)
class InfoFeatureEntryImpl @Inject constructor(
    private val updaterCardApi: UpdaterCardApi,
    private val settingFeatureEntry: SettingsFeatureEntry
) : InfoFeatureEntry {

    private val deeplinkKey = "{${DeeplinkConstants.KEY}}"

    private fun start(): String = "@${ROUTE.name}"

    override fun fullInfo(): String = "@${ROUTE.name}full"

    override fun NavGraphBuilder.navigation(navController: NavHostController) {
        navigation(startDestination = start(), route = ROUTE.name) {
            composable("@${ROUTE.name}") {
                ComposableDeviceInfoScreen(
                    updaterCardApi,
                    onOpenFullDeviceInfo = { navController.navigate(fullInfo()) },
                    onOpenOptions = { navController.navigate(settingFeatureEntry.ROUTE.name) }
                )
            }
            composable("@${ROUTE.name}full") {
                ComposableFullDeviceInfoScreen(navController)
            }
            composable(
                route = "@${ROUTE.name}/$deeplinkKey",
                deepLinks = listOf(
                    navDeepLink {
                        uriPattern = "${DeeplinkConstants.LAB_FLIPPER_NET}/$deeplinkKey"
                        action = Intent.ACTION_VIEW
                    },
                    navDeepLink {
                        uriPattern = "${DeeplinkConstants.MY_FLIPPER_DEV}/$deeplinkKey"
                        action = Intent.ACTION_VIEW
                    }
                )
            ) {
                ComposableDeviceInfoScreen(
                    updaterCardApi,
                    onOpenFullDeviceInfo = { navController.navigate(fullInfo()) },
                    onOpenOptions = { navController.navigate(settingFeatureEntry.ROUTE.name) }
                )
            }
        }
    }
}
