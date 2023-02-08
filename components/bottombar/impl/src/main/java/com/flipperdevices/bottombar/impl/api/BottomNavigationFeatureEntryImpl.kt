package com.flipperdevices.bottombar.impl.api

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.flipperdevices.bottombar.api.BottomNavigationFeatureEntry
import com.flipperdevices.bottombar.impl.composable.ComposableMainScreen
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

@ContributesBinding(AppGraph::class, BottomNavigationFeatureEntry::class)
@ContributesMultibinding(AppGraph::class, ComposableFeatureEntry::class)
class BottomNavigationFeatureEntryImpl @Inject constructor(
    featureEntriesProvider: Provider<MutableSet<AggregateFeatureEntry>>,
    composableEntriesProvider: Provider<MutableSet<ComposableFeatureEntry>>,
    private val connectionApi: ConnectionApi,
    private val notificationRenderer: InAppNotificationRenderer
) : BottomNavigationFeatureEntry {
    private val featureEntriesMutable by featureEntriesProvider
    private val composableEntriesMutable by composableEntriesProvider

    override fun start(): String = "@${ROUTE.name}"

    override fun NavGraphBuilder.composable(navController: NavHostController) {
        composable(route = "@${ROUTE.name}") {
            ComposableMainScreen(
                connectionApi = connectionApi,
                featureEntries = featureEntriesMutable.toPersistentSet(),
                composableEntries = composableEntriesMutable.toPersistentSet(),
                notificationRenderer = notificationRenderer
            )
        }
    }
}
