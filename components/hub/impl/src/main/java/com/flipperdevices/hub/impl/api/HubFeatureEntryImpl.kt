package com.flipperdevices.hub.impl.api

import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ui.navigation.AggregateFeatureEntry
import com.flipperdevices.deeplink.model.DeeplinkConstants
import com.flipperdevices.deeplink.model.DeeplinkNavType
import com.flipperdevices.hub.api.HubDecomposeComponent
import com.flipperdevices.hub.api.HubFeatureEntry
import com.flipperdevices.ui.decompose.rememberComponentContext
import com.squareup.anvil.annotations.ContributesBinding
import com.squareup.anvil.annotations.ContributesMultibinding
import javax.inject.Inject

@ContributesBinding(AppGraph::class, HubFeatureEntry::class)
@ContributesMultibinding(AppGraph::class, AggregateFeatureEntry::class)
class HubFeatureEntryImpl @Inject constructor(
    private val hubDecomposeComponentFactory: HubDecomposeComponent.Factory
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
                val componentContext = rememberComponentContext()
                val fileManagerComponent = remember(componentContext) {
                    hubDecomposeComponentFactory(componentContext) as HubDecomposeComponentImpl
                }
                val childStack by fileManagerComponent.stack.subscribeAsState()

                Children(
                    stack = childStack
                ) {
                    it.instance.Render()
                }
            }
        }
    }
}
