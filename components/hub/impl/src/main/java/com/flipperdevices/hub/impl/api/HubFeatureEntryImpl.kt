package com.flipperdevices.hub.impl.api

import androidx.compose.foundation.layout.padding
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
import com.flipperdevices.metric.api.MetricApi
import com.flipperdevices.metric.api.events.SimpleEvent
import com.flipperdevices.nfc.attack.api.NFCAttackFeatureEntry
import com.flipperdevices.screenstreaming.api.ScreenStreamingFeatureEntry
import com.squareup.anvil.annotations.ContributesBinding
import com.squareup.anvil.annotations.ContributesMultibinding
import javax.inject.Inject

@ContributesBinding(AppGraph::class, HubFeatureEntry::class)
@ContributesMultibinding(AppGraph::class, AggregateFeatureEntry::class)
class HubFeatureEntryImpl @Inject constructor(
    private val nfcAttackFeatureEntry: NFCAttackFeatureEntry,
    private val mainCardApi: MainCardApi,
    private val screenStreamingFeatureEntry: ScreenStreamingFeatureEntry,
    private val fapHubMainScreenApi: FapHubMainScreenApi,
    private val metricApi: MetricApi
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
                ComposableHub(
                    onOpenAttack = {
                        navController.navigate(nfcAttackFeatureEntry.ROUTE.name)
                    },
                    mainCardComposable = {
                        mainCardApi.ComposableMainCard(
                            modifier = Modifier.padding(
                                start = 14.dp,
                                end = 14.dp,
                                top = 14.dp
                            ),
                            onClick = {
                                metricApi.reportSimpleEvent(SimpleEvent.OPEN_FAPHUB)
                                navController.navigate(fapHubMainScreenApi.ROUTE.name)
                            }
                        )
                    },
                    onOpenRemoteControl = {
                        globalNavController.navigate(screenStreamingFeatureEntry.ROUTE.name)
                    }
                )
            }
        }
    }
}
