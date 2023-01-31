package com.flipperdevices.archive.impl.api

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.flipperdevices.archive.api.ArchiveFeatureEntry
import com.flipperdevices.archive.api.CategoryFeatureEntry
import com.flipperdevices.archive.api.SearchFeatureEntry
import com.flipperdevices.archive.impl.composable.ComposableArchive
import com.flipperdevices.bridge.synchronization.api.SynchronizationUiApi
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ui.navigation.AggregateFeatureEntry
import com.flipperdevices.keyscreen.api.KeyScreenFeatureEntry
import com.squareup.anvil.annotations.ContributesBinding
import com.squareup.anvil.annotations.ContributesMultibinding
import javax.inject.Inject

@ContributesBinding(AppGraph::class, ArchiveFeatureEntry::class)
@ContributesMultibinding(AppGraph::class, AggregateFeatureEntry::class)
class ArchiveFeatureEntryImpl @Inject constructor(
    private val synchronizationUiApi: SynchronizationUiApi,
    private val keyScreenFeatureEntry: KeyScreenFeatureEntry,
    private val searchFeatureEntry: SearchFeatureEntry,
    private val categoryFeatureEntry: CategoryFeatureEntry
) : ArchiveFeatureEntry {
    override fun getArchiveScreen(): String {
        return "@${ROUTE.name}"
    }

    override fun NavGraphBuilder.navigation(navController: NavHostController) {
        navigation(
            route = ROUTE.name,
            startDestination = getArchiveScreen()
        ) {
            composable(
                route = getArchiveScreen()
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
