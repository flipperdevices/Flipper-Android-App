package com.flipperdevices.archive.impl.api

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import androidx.navigation.navigation
import com.flipperdevices.archive.api.ArchiveFeatureEntry
import com.flipperdevices.archive.api.CategoryFeatureEntry
import com.flipperdevices.archive.api.SearchFeatureEntry
import com.flipperdevices.archive.impl.composable.ComposableArchive
import com.flipperdevices.bridge.synchronization.api.SynchronizationUiApi
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ui.navigation.AggregateFeatureEntry
import com.flipperdevices.deeplink.model.Deeplink
import com.flipperdevices.deeplink.model.DeeplinkConstants
import com.flipperdevices.deeplink.model.DeeplinkNavType
import com.flipperdevices.keyscreen.api.KeyScreenFeatureEntry
import com.squareup.anvil.annotations.ContributesBinding
import com.squareup.anvil.annotations.ContributesMultibinding
import javax.inject.Inject

private const val DEEPLINK_SCHEME = DeeplinkConstants.SCHEMA
internal const val DEEPLINK_ARCHIVE_URL = "${DEEPLINK_SCHEME}archive"

@ContributesBinding(AppGraph::class, ArchiveFeatureEntry::class)
@ContributesMultibinding(AppGraph::class, AggregateFeatureEntry::class)
class ArchiveFeatureEntryImpl @Inject constructor(
    private val synchronizationUiApi: SynchronizationUiApi,
    private val keyScreenFeatureEntry: KeyScreenFeatureEntry,
    private val searchFeatureEntry: SearchFeatureEntry,
    private val categoryFeatureEntry: CategoryFeatureEntry
) : ArchiveFeatureEntry {
    private val deeplinkKey = DeeplinkConstants.KEY
    override fun getArchiveScreen(): String {
        return "@${ROUTE.name}?$deeplinkKey={$deeplinkKey}"
    }

    override fun getDeeplinkPattern(): String = DEEPLINK_ARCHIVE_URL

    private val archiveArguments = listOf(
        navArgument(deeplinkKey) {
            type = DeeplinkNavType()
            nullable = true
        }
    )

    private val deeplinkArguments = listOf(
        navDeepLink {
            uriPattern = DEEPLINK_ARCHIVE_URL
        }
    )

    override fun NavGraphBuilder.navigation(navController: NavHostController) {
        navigation(
            route = ROUTE.name,
            startDestination = getArchiveScreen()
        ) {
            composable(
                route = getArchiveScreen(),
                arguments = archiveArguments,
                deepLinks = deeplinkArguments
            ) {
                ComposableArchive(
                    synchronizationUiApi = synchronizationUiApi,
                    onOpenSearchScreen = {
                        val searchScreen = searchFeatureEntry.getSearchScreen(false)
                        navController.navigate(searchScreen)
                    },
                    onOpenKeyScreen = { flipperKeyPath ->
                        val keyScreen = keyScreenFeatureEntry.getKeyScreen(flipperKeyPath)
                        navController.navigate(keyScreen)
                    },
                    onOpenCategory = { categoryType ->
                        val categoryScreen = categoryFeatureEntry.getCategoryScreen(categoryType)
                        navController.navigate(categoryScreen)
                    }
                )
            }
        }
    }
}
