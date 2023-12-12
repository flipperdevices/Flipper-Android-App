package com.flipperdevices.settings.impl.api

import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ui.navigation.AggregateFeatureEntry
import com.flipperdevices.settings.api.SettingsFeatureEntry
import com.flipperdevices.ui.decompose.rememberComponentContext
import com.squareup.anvil.annotations.ContributesBinding
import com.squareup.anvil.annotations.ContributesMultibinding
import javax.inject.Inject

@ContributesBinding(AppGraph::class, SettingsFeatureEntry::class)
@ContributesMultibinding(AppGraph::class, AggregateFeatureEntry::class)
class SettingsFeatureEntryImpl @Inject constructor(
    private val settingsFactory: SettingsDecomposeComponent.Factory
) : SettingsFeatureEntry {
    private fun start() = "@${ROUTE.name}"

    override fun NavGraphBuilder.navigation(navController: NavHostController) {
        navigation(startDestination = start(), route = ROUTE.name) {
            composable("@${ROUTE.name}") {
                val componentContext = rememberComponentContext()
                val fileManagerComponent = remember(componentContext) {
                    settingsFactory(componentContext)
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
