package com.flipperdevices.faphub.fapscreen.impl.api

import android.net.Uri
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.flipperdevices.bottombar.api.BottomNavigationHandleDeeplink
import com.flipperdevices.bottombar.model.BottomBarTab
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ui.navigation.ComposableFeatureEntry
import com.flipperdevices.deeplink.model.DeeplinkConstants
import com.flipperdevices.faphub.fapscreen.api.FapScreenApi
import com.flipperdevices.faphub.fapscreen.impl.composable.ComposableFapScreen
import com.flipperdevices.faphub.installation.button.api.FapButtonSize
import com.flipperdevices.faphub.installation.button.api.FapInstallationUIApi
import com.flipperdevices.faphub.installation.button.api.toFapButtonConfig
import com.flipperdevices.faphub.uninstallbutton.api.FapUninstallApi
import com.squareup.anvil.annotations.ContributesBinding
import com.squareup.anvil.annotations.ContributesMultibinding
import javax.inject.Inject

internal const val FAP_ID_KEY = "fap_id"
private const val DEEPLINK_SCHEME = DeeplinkConstants.SCHEMA
private const val DEEPLINK_FAP_ID = "${DEEPLINK_SCHEME}fap_id={$FAP_ID_KEY}"

@ContributesBinding(AppGraph::class, FapScreenApi::class)
@ContributesMultibinding(AppGraph::class, ComposableFeatureEntry::class)
class FapScreenApiImpl @Inject constructor(
    private val installationUIApi: FapInstallationUIApi,
    private val bottomBarApi: BottomNavigationHandleDeeplink,
    private val uninstallApi: FapUninstallApi
) : FapScreenApi {
    private val fapArguments = listOf(
        navArgument(FAP_ID_KEY) {
            type = NavType.Companion.StringType
            nullable = false
        }
    )

    override fun getFapScreen(
        id: String
    ) = "@${ROUTE.name}?fap_id=${Uri.encode(id)}"

    override fun getFapScreenByDeeplink(id: String): String {
        return "${DEEPLINK_SCHEME}fap_id=$id"
    }

    private val deeplinkArguments = listOf(
        navDeepLink {
            uriPattern = DEEPLINK_FAP_ID
        }
    )

    override fun NavGraphBuilder.composable(navController: NavHostController) {
        composable(
            route = "@${ROUTE.name}?fap_id={$FAP_ID_KEY}",
            arguments = fapArguments,
            deepLinks = deeplinkArguments
        ) {
            ComposableFapScreen(
                navController = navController,
                onBack = navController::popBackStack,
                installationButton = { fapItem, modifier ->
                    installationUIApi.ComposableButton(
                        config = fapItem?.toFapButtonConfig(),
                        modifier = modifier,
                        fapButtonSize = FapButtonSize.LARGE
                    )
                },
                onOpenDeviceTab = { bottomBarApi.onChangeTab(BottomBarTab.DEVICE, force = true) },
                uninstallButton = { modifier, fapItem ->
                    uninstallApi.ComposableFapUninstallButton(
                        modifier = modifier,
                        fapItem = fapItem
                    )
                }
            )
        }
    }
}
