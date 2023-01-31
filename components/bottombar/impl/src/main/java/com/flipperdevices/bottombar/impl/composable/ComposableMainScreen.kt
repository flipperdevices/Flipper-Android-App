package com.flipperdevices.bottombar.impl.composable

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.flipperdevices.bottombar.impl.main.compose.ComposeBottomBar
import com.flipperdevices.bottombar.impl.main.viewmodel.BottomNavigationViewModel
import com.flipperdevices.bottombar.impl.model.FlipperBottomTab
import com.flipperdevices.connection.api.ConnectionApi
import com.flipperdevices.core.ui.navigation.AggregateFeatureEntry
import com.flipperdevices.core.ui.navigation.ComposableFeatureEntry
import kotlinx.collections.immutable.ImmutableSet
import tangle.viewmodel.compose.tangleViewModel

@Composable
fun ComposableMainScreen(
    connectionApi: ConnectionApi,
    featureEntries: ImmutableSet<AggregateFeatureEntry>,
    composableEntries: ImmutableSet<ComposableFeatureEntry>,
    modifier: Modifier = Modifier,
    navigationViewModel: BottomNavigationViewModel = tangleViewModel()
) {
    val selectedTab by navigationViewModel.selectedTab.collectAsState()

    Scaffold(
        modifier = modifier,
        bottomBar = {
            ComposeBottomBar(
                connectionApi = connectionApi,
                selectedItem = selectedTab,
                onBottomBarClick = navigationViewModel::onSelectTab
            )
        }
    ) {
        ComposableTab(
            modifier = Modifier.padding(it),
            featureEntries = featureEntries,
            composableEntries = composableEntries,
            tab = selectedTab
        )
    }
}

@Composable
fun ComposableTab(
    tab: FlipperBottomTab,
    featureEntries: ImmutableSet<AggregateFeatureEntry>,
    composableEntries: ImmutableSet<ComposableFeatureEntry>,
    modifier: Modifier = Modifier,
) = key(tab) {
    val navController = rememberNavController()
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = tab.startRoute.name
    ) {
        featureEntries.forEach {
            with(it) {
                navigation(navController)
            }
        }
        composableEntries.forEach {
            with(it) {
                composable(navController)
            }
        }
    }
}