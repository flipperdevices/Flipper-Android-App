package com.flipperdevices.bottombar.impl.api

import android.content.Intent
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.flipperdevices.bottombar.api.BottomNavigationFeatureEntry
import com.flipperdevices.bottombar.api.BottomNavigationHandleDeeplink
import com.flipperdevices.bottombar.impl.composable.ComposableMainScreen
import com.flipperdevices.bottombar.impl.model.FlipperBottomTab
import com.flipperdevices.bottombar.model.BottomBarTab
import com.flipperdevices.connection.api.ConnectionApi
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.di.provideDelegate
import com.flipperdevices.core.ui.navigation.AggregateFeatureEntry
import com.flipperdevices.core.ui.navigation.ComposableFeatureEntry
import com.flipperdevices.inappnotification.api.InAppNotificationRenderer
import com.squareup.anvil.annotations.ContributesBinding
import com.squareup.anvil.annotations.ContributesMultibinding
import kotlinx.collections.immutable.toPersistentSet
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

@Singleton
@ContributesBinding(AppGraph::class, BottomNavigationFeatureEntry::class)
@ContributesBinding(AppGraph::class, BottomNavigationHandleDeeplink::class)
@ContributesMultibinding(AppGraph::class, ComposableFeatureEntry::class)
class BottomNavigationFeatureEntryImpl @Inject constructor(
    featureEntriesProvider: Provider<MutableSet<AggregateFeatureEntry>>,
    composableEntriesProvider: Provider<MutableSet<ComposableFeatureEntry>>,
    private val connectionApi: ConnectionApi,
    private val notificationRenderer: InAppNotificationRenderer
) : BottomNavigationFeatureEntry, BottomNavigationHandleDeeplink {
    private val featureEntriesMutable by featureEntriesProvider
    private val composableEntriesMutable by composableEntriesProvider

    private var childNavController: NavHostController? = null

    override fun start(): String = "@${ROUTE.name}"
    override fun NavGraphBuilder.composable(navController: NavHostController) {
        composable(route = start()) {
            val childNavController = rememberNavController().also {
                this@BottomNavigationFeatureEntryImpl.childNavController = it
            }
            ComposableMainScreen(
                connectionApi = connectionApi,
                featureEntries = featureEntriesMutable.toPersistentSet(),
                composableEntries = composableEntriesMutable.toPersistentSet(),
                notificationRenderer = notificationRenderer,
                navController = childNavController,
                onTabClick = { tab, force -> onChangeTab(tab, force) }
            )
        }
    }

    override fun onChangeTab(tab: BottomBarTab, force: Boolean) {
        onChangeTab(
            tab = when (tab) {
                BottomBarTab.DEVICE -> FlipperBottomTab.DEVICE
                BottomBarTab.ARCHIVE -> FlipperBottomTab.ARCHIVE
                BottomBarTab.HUB -> FlipperBottomTab.HUB
            },
            force = force
        )
    }

    private fun onChangeTab(
        tab: FlipperBottomTab,
        force: Boolean
    ) {
        childNavController?.let { navController ->
            val topLevelNavOptions = navOptions {
                // Pop up to the start destination of the graph to
                // avoid building up a large stack of destinations
                // on the back stack as users select items
                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = force.not()
                }
                // Avoid multiple copies of the same destination when
                // reselecting the same item
                launchSingleTop = true
                // Restore state when reselecting a previously selected item
                restoreState = force.not()
            }
            navController.navigate(tab.startRoute.name, topLevelNavOptions)
        }
    }

    override fun handleDeepLink(intent: Intent) {
        childNavController?.handleDeepLink(intent)
    }
}
