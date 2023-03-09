package com.flipperdevices.analytics.shake2report.impl.api

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.flipperdevices.analytics.shake2report.impl.composable.Shake2ReportScreen
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ui.navigation.ComposableFeatureEntry
import com.flipperdevices.shake2report.api.Shake2ReportFeatureEntry
import com.squareup.anvil.annotations.ContributesBinding
import com.squareup.anvil.annotations.ContributesMultibinding
import javax.inject.Inject

@ContributesBinding(AppGraph::class, Shake2ReportFeatureEntry::class)
@ContributesMultibinding(AppGraph::class, ComposableFeatureEntry::class)
class Shake2ReportFeatureEntryImpl @Inject constructor() : Shake2ReportFeatureEntry {
    override fun start() = "@$ROUTE"

    override fun NavGraphBuilder.composable(navController: NavHostController) {
        composable(route = start()) {
            Shake2ReportScreen(onBack = navController::popBackStack)
        }
    }
}
