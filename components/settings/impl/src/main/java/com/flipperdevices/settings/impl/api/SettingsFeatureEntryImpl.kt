package com.flipperdevices.settings.impl.api

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ui.navigation.AggregateFeatureEntry
import com.flipperdevices.settings.api.SettingsFeatureEntry
import com.flipperdevices.settings.impl.composable.ComposableCommonSetting
import com.squareup.anvil.annotations.ContributesBinding
import com.squareup.anvil.annotations.ContributesMultibinding
import javax.inject.Inject

@ContributesBinding(AppGraph::class, SettingsFeatureEntry::class)
@ContributesMultibinding(AppGraph::class, AggregateFeatureEntry::class)
class SettingsFeatureEntryImpl @Inject constructor() : SettingsFeatureEntry {
    private fun start() = "@${ROUTE.name}"
    override fun NavGraphBuilder.navigation(navController: NavHostController) {
        navigation(startDestination = start(), route = ROUTE.name) {
            composable("@${ROUTE.name}") {
                ComposableCommonSetting(navController)
            }
        }
    }
}
