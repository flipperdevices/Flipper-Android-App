package com.flipperdevices.widget.screen.api

import android.net.Uri
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.flipperdevices.archive.api.ArchiveApi
import com.flipperdevices.archive.api.SearchFeatureEntry
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ui.navigation.ComposableFeatureEntry
import com.flipperdevices.core.ui.navigation.GetOnceResult
import com.flipperdevices.deeplink.model.Deeplink
import com.flipperdevices.deeplink.model.DeeplinkConstants
import com.flipperdevices.deeplink.model.DeeplinkNavType
import com.flipperdevices.widget.api.WidgetFeatureEntry
import com.flipperdevices.widget.screen.compose.WidgetOptionsComposable
import com.flipperdevices.widget.screen.viewmodel.WidgetSelectViewModel
import com.squareup.anvil.annotations.ContributesBinding
import com.squareup.anvil.annotations.ContributesMultibinding
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import tangle.viewmodel.compose.tangleViewModel
import javax.inject.Inject

private const val DEEPLINK_KEY = DeeplinkConstants.KEY
private const val DEEPLINK_SCHEME = DeeplinkConstants.SCHEMA
private const val DEEPLINK_WIDGET_URL = "${DEEPLINK_SCHEME}widget={$DEEPLINK_KEY}"

@ContributesBinding(AppGraph::class, WidgetFeatureEntry::class)
@ContributesMultibinding(AppGraph::class, ComposableFeatureEntry::class)
class WidgetFeatureEntryImpl @Inject constructor(
    private val archiveApi: ArchiveApi,
    private val searchFeatureEntry: SearchFeatureEntry
) : WidgetFeatureEntry {
    override fun getWidgetScreenByDeeplink(deeplink: Deeplink): String {
        val deeplinkStr = Uri.encode(Json.encodeToString(deeplink))
        return "${DEEPLINK_SCHEME}widget=$deeplinkStr"
    }

    private val widgetRoute = "@${ROUTE.name}/$DEEPLINK_KEY={$DEEPLINK_KEY}"

    private val widgetArguments = listOf(
        navArgument(DEEPLINK_KEY) {
            type = DeeplinkNavType()
            nullable = true
        }
    )

    private val deeplinkArguments = listOf(
        navDeepLink { uriPattern = DEEPLINK_WIDGET_URL }
    )

    override fun NavGraphBuilder.composable(navController: NavHostController) {
        composable(
            route = widgetRoute,
            arguments = widgetArguments,
            deepLinks = deeplinkArguments
        ) {
            val widgetSelectViewModel: WidgetSelectViewModel = tangleViewModel()
            navController.GetOnceResult<FlipperKeyPath>(SearchFeatureEntry.SEARCH_RESULT_KEY) {
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
