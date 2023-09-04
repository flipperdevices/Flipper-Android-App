package com.flipperdevices.bottombar.impl.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.createGraph
import com.flipperdevices.bottombar.impl.composable.bottombar.ComposeBottomBar
import com.flipperdevices.bottombar.impl.model.FlipperBottomTab
import com.flipperdevices.bottombar.impl.viewmodel.BottomNavigationViewModel
import com.flipperdevices.connection.api.ConnectionApi
import com.flipperdevices.core.ui.navigation.AggregateFeatureEntry
import com.flipperdevices.core.ui.navigation.ComposableFeatureEntry
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.inappnotification.api.InAppNotificationRenderer
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.collections.immutable.ImmutableSet
import tangle.viewmodel.compose.tangleViewModel

@Composable
fun ComposableMainScreen(
    connectionApi: ConnectionApi,
    notificationRenderer: InAppNotificationRenderer,
    featureEntries: ImmutableSet<AggregateFeatureEntry>,
    composableEntries: ImmutableSet<ComposableFeatureEntry>,
    navController: NavHostController,
    onTabClick: (tab: FlipperBottomTab, force: Boolean) -> Unit,
    modifier: Modifier = Modifier,
    navigationViewModel: BottomNavigationViewModel = tangleViewModel()
) {
    val selectedTab by navigationViewModel.selectedTab.collectAsState()
    val startDestination = remember { navigationViewModel.getStartDestination().startRoute.name }
    val currentDestination by navController.currentBackStackEntryAsState()
    key(currentDestination?.destination) {
        navigationViewModel.invalidateSelectedTab(currentDestination?.destination)
    }

    Scaffold(
        modifier = modifier,
        bottomBar = {
            ComposeBottomBar(
                connectionApi = connectionApi,
                selectedItem = selectedTab,
                onBottomBarClick = {
                    onTabClick(it, selectedTab == it)
                }
            )
        }
    ) {
        Box(modifier = Modifier.padding(it).fillMaxSize()) {
            val graph = remember(startDestination, featureEntries, composableEntries) {
                navController.createGraph(startDestination, null) {
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
            NavHost(
                navController = navController,
                graph = graph
            )

            ComposableInAppNotification(
                modifier = Modifier.align(Alignment.BottomCenter),
                notificationRenderer = notificationRenderer
            )
            connectionApi.CheckAndShowUnsupportedDialog()
        }
    }
    val systemUIController = rememberSystemUiController()
    systemUIController.setNavigationBarColor(
        color = LocalPallet.current.bottomBarBackground,
    )
}
