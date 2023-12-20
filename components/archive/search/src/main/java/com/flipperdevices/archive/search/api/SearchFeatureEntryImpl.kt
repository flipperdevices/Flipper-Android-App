package com.flipperdevices.archive.search.api

import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.flipperdevices.archive.api.SearchFeatureEntry
import com.flipperdevices.archive.search.composable.ComposableSearch
import com.flipperdevices.archive.search.viewmodel.SearchViewModel
import com.flipperdevices.bridge.synchronization.api.SynchronizationUiApi
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ui.navigation.ComposableFeatureEntry
import com.flipperdevices.core.ui.navigation.setOnResult
import com.squareup.anvil.annotations.ContributesBinding
import com.squareup.anvil.annotations.ContributesMultibinding
import tangle.viewmodel.compose.tangleViewModel
import javax.inject.Inject

internal const val IS_EXIT_ON_OPEN_KEY = "exist_on_open"

@ContributesBinding(AppGraph::class, SearchFeatureEntry::class)
@ContributesMultibinding(AppGraph::class, ComposableFeatureEntry::class)
class SearchFeatureEntryImpl @Inject constructor(
    private val synchronizationUiApi: SynchronizationUiApi,
    private val keyScreenFeatureEntry: KeyScreenFeatureEntry,
) : SearchFeatureEntry {

    override fun getSearchScreen(isExitOnOpenKey: Boolean): String {
        return "@${ROUTE.name}?is_exit_on_open_key=$isExitOnOpenKey"
    }

    private val searchArguments = listOf(
        navArgument(IS_EXIT_ON_OPEN_KEY) {
            nullable = false
            type = NavType.BoolType
        }
    )

    override fun NavGraphBuilder.composable(navController: NavHostController) {
        composable(
            route = "@${ROUTE.name}?is_exit_on_open_key={$IS_EXIT_ON_OPEN_KEY}",
            arguments = searchArguments
        ) {
            val keyboard = LocalSoftwareKeyboardController.current
            val isExitOnOpenKey = it.arguments?.getBoolean(IS_EXIT_ON_OPEN_KEY) ?: false
            val searchViewModel: SearchViewModel = tangleViewModel()

            ComposableSearch(
                searchViewModel = searchViewModel,
                synchronizationUiApi = synchronizationUiApi,
                onBack = {
                    keyboard?.hide()
                    navController.popBackStack()
                },
                onOpenKeyScreen = { flipperKeyPath ->
                    keyboard?.hide()
                    val keyScreen = keyScreenFeatureEntry.getKeyScreen(flipperKeyPath)
                    if (isExitOnOpenKey) {
                        navController.setOnResult(
                            key = SearchFeatureEntry.SEARCH_RESULT_KEY,
                            value = flipperKeyPath
                        )
                    } else {
                        navController.navigate(keyScreen)
                    }
                }
            )
        }
    }
}
