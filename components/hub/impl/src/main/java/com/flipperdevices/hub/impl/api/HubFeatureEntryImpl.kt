package com.flipperdevices.hub.impl.api

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ui.navigation.AggregateFeatureEntry
import com.flipperdevices.core.ui.navigation.LocalGlobalNavigationNavStack
import com.flipperdevices.deeplink.model.DeeplinkConstants
import com.flipperdevices.deeplink.model.DeeplinkNavType
import com.flipperdevices.faphub.main.api.FapHubMainScreenApi
import com.flipperdevices.faphub.maincard.api.MainCardApi
import com.flipperdevices.hub.api.HubFeatureEntry
import com.flipperdevices.hub.impl.composable.ComposableHub
import com.flipperdevices.hub.impl.viewmodel.HubViewModel
import com.flipperdevices.nfc.attack.api.NFCAttackFeatureEntry
import com.flipperdevices.screenstreaming.api.ScreenStreamingFeatureEntry
import com.squareup.anvil.annotations.ContributesBinding
import com.squareup.anvil.annotations.ContributesMultibinding
import tangle.viewmodel.compose.tangleViewModel
import javax.inject.Inject

@ContributesBinding(AppGraph::class, HubFeatureEntry::class)
@ContributesMultibinding(AppGraph::class, AggregateFeatureEntry::class)
class HubFeatureEntryImpl @Inject constructor(
    private val nfcAttackFeatureEntry: NFCAttackFeatureEntry,
    private val mainCardApi: MainCardApi,
    private val screenStreamingFeatureEntry: ScreenStreamingFeatureEntry,
    private val fapHubMainScreenApi: FapHubMainScreenApi
) : HubFeatureEntry {
    override fun start() = "@${ROUTE.name}"

    private val hubArguments = listOf(
        navArgument(DeeplinkConstants.KEY) {
            type = DeeplinkNavType()
            nullable = true
        }
    )

    override fun NavGraphBuilder.navigation(navController: NavHostController) {
        navigation(
            startDestination = start(),
            route = ROUTE.name
        ) {
            composable(
                route = start(),
                arguments = hubArguments
            ) {
                val globalNavController = LocalGlobalNavigationNavStack.current
                val hubViewModel = tangleViewModel<HubViewModel>()
                val isApplicationCatalogEnabled by hubViewModel.isApplicationCatalogEnabledFlow()
                    .collectAsState()
                ComposableHub(
                    onOpenAttack = {
                        navController.navigate(nfcAttackFeatureEntry.ROUTE.name)
                    },
                    mainCardComposable = {
                        if (isApplicationCatalogEnabled) {
                            mainCardApi.ComposableMainCard(
                                modifier = Modifier.padding(
                                    start = 14.dp,
                                    end = 14.dp,
                                    top = 14.dp
                                ),
                                onClick = {
                                    navController.navigate(fapHubMainScreenApi.ROUTE.name)
                                }
                            )
                        }
                    },
                    onOpenRemoteControl = {
                        globalNavController.navigate(screenStreamingFeatureEntry.ROUTE.name)
                    }
                )
            }
        }
    }
}
