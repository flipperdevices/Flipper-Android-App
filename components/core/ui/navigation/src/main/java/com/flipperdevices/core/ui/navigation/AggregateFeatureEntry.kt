package com.flipperdevices.core.ui.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController

interface AggregateFeatureEntry : FeatureEntry {
    val ROUTE: FeatureScreenRootRoute

    fun NavGraphBuilder.navigation(
        navController: NavHostController
    )
}
