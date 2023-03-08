package com.flipperdevices.bottombar.impl.api

import android.content.Intent
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.flipperdevices.bottombar.api.BottomNavigationFeatureEntry
import com.flipperdevices.bottombar.api.BottomNavigationHandleDeeplink
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
                navController = childNavController
            )
        }
    }

    override fun handleDeepLink(intent: Intent) {
        childNavController?.handleDeepLink(intent)
    }
}
