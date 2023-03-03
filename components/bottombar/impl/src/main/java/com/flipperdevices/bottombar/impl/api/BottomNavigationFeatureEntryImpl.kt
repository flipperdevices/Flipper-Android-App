package com.flipperdevices.bottombar.impl.api

import android.net.Uri
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import androidx.navigation.navigation
import com.flipperdevices.bottombar.api.BottomNavigationFeatureEntry
import com.flipperdevices.bottombar.impl.composable.ComposableMainScreen
import com.flipperdevices.connection.api.ConnectionApi
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.di.provideDelegate
import com.flipperdevices.core.ktx.android.parcelable
import com.flipperdevices.core.ui.navigation.AggregateFeatureEntry
import com.flipperdevices.core.ui.navigation.ComposableFeatureEntry
import com.flipperdevices.deeplink.model.Deeplink
import com.flipperdevices.deeplink.model.DeeplinkConstants
import com.flipperdevices.deeplink.model.DeeplinkNavType
import com.flipperdevices.inappnotification.api.InAppNotificationRenderer
import com.squareup.anvil.annotations.ContributesBinding
import com.squareup.anvil.annotations.ContributesMultibinding
import kotlinx.collections.immutable.toPersistentSet
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Provider

private const val DEEPLINK_KEY = DeeplinkConstants.KEY
private const val DEEPLINK_SCHEME = DeeplinkConstants.SCHEMA
private const val DEEPLINK_BOTTOM_BAR_URL = "${DEEPLINK_SCHEME}bottom_bar{$DEEPLINK_KEY}"

@ContributesBinding(AppGraph::class, BottomNavigationFeatureEntry::class)
@ContributesMultibinding(AppGraph::class, AggregateFeatureEntry::class)
class BottomNavigationFeatureEntryImpl @Inject constructor(
    featureEntriesProvider: Provider<MutableSet<AggregateFeatureEntry>>,
    composableEntriesProvider: Provider<MutableSet<ComposableFeatureEntry>>,
    private val connectionApi: ConnectionApi,
    private val notificationRenderer: InAppNotificationRenderer
) : BottomNavigationFeatureEntry {
    private val featureEntriesMutable by featureEntriesProvider
    private val composableEntriesMutable by composableEntriesProvider

    private val bottomNavigationRoute = "@${ROUTE.name}?$DEEPLINK_KEY={$DEEPLINK_KEY}"

    override fun start(deeplink: Deeplink?): String {
        val deeplinkStr = Uri.encode(Json.encodeToString(deeplink))
        return "@${ROUTE.name}?$DEEPLINK_KEY=$deeplinkStr"
    }

    private val bottomNavigationArguments = listOf(
        navArgument(DEEPLINK_KEY) {
            type = DeeplinkNavType()
            nullable = true
        }
    )

    private val deeplinkArguments = listOf(
        navDeepLink {
            uriPattern = DEEPLINK_BOTTOM_BAR_URL
        }
    )

    override fun NavGraphBuilder.navigation(navController: NavHostController) {
        navigation(route = ROUTE.name, startDestination = bottomNavigationRoute) {
            composable(
                route = bottomNavigationRoute,
                arguments = bottomNavigationArguments,
                deepLinks = deeplinkArguments
            ) {
                ComposableMainScreen(
                    connectionApi = connectionApi,
                    featureEntries = featureEntriesMutable.toPersistentSet(),
                    composableEntries = composableEntriesMutable.toPersistentSet(),
                    notificationRenderer = notificationRenderer
                )
            }
        }
    }
}
