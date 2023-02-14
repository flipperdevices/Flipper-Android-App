package com.flipperdevices.widget.screen.api

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.flipperdevices.archive.api.ArchiveApi
import com.flipperdevices.archive.api.SearchApi
import com.flipperdevices.archive.api.SearchFeatureEntry
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ui.navigation.ComposableFeatureEntry
import com.flipperdevices.core.ui.navigation.GetOnceResult
import com.flipperdevices.widget.api.WidgetFeatureEntry
import com.flipperdevices.widget.screen.compose.WidgetOptionsComposable
import com.flipperdevices.widget.screen.viewmodel.WidgetSelectViewModel
import com.squareup.anvil.annotations.ContributesBinding
import com.squareup.anvil.annotations.ContributesMultibinding
import tangle.viewmodel.compose.tangleViewModel
import javax.inject.Inject

internal const val EXTRA_WIDGET_ID_KEY = "widget_id"

@ContributesBinding(AppGraph::class, WidgetFeatureEntry::class)
@ContributesMultibinding(AppGraph::class, ComposableFeatureEntry::class)
class WidgetFeatureEntryImpl @Inject constructor(
    private val archiveApi: ArchiveApi,
    private val searchFeatureEntry: SearchFeatureEntry
) : WidgetFeatureEntry {
    override fun getWidgetScreen(widgetId: Int): String {
        return "@${ROUTE.name}?id=$widgetId"
    }

    private val widgetArguments = listOf(
        navArgument(EXTRA_WIDGET_ID_KEY) {
            type = NavType.IntType
            nullable = false
        }
    )

    override fun NavGraphBuilder.composable(navController: NavHostController) {
        composable(
            route = "@${ROUTE.name}?id={$EXTRA_WIDGET_ID_KEY}",
            arguments = widgetArguments
        ) {
            val widgetSelectViewModel: WidgetSelectViewModel = tangleViewModel()
            navController.GetOnceResult<FlipperKeyPath>(SearchApi.SEARCH_RESULT_KEY) {
                widgetSelectViewModel.onSelectKey(it)
            }
            WidgetOptionsComposable(
                archiveApi = archiveApi,
                widgetSelectViewModel = widgetSelectViewModel,
                onOpenSearchScreen = {
                    val searchScreen = searchFeatureEntry.getSearchScreen(isExitOnOpenKey = true)
                    navController.navigate(searchScreen)
                }
            )
        }
    }
}
