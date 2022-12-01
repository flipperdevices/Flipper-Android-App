package com.flipperdevices.core.ui.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController

interface ComposableFeatureEntry : FeatureEntry {
    fun NavGraphBuilder.composable(navController: NavHostController)
}
