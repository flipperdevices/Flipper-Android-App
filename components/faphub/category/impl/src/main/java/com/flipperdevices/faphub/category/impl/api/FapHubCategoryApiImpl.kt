package com.flipperdevices.faphub.category.impl.api

import android.net.Uri
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ui.navigation.ComposableFeatureEntry
import com.flipperdevices.faphub.category.api.FapHubCategoryApi
import com.flipperdevices.faphub.category.impl.composable.ComposableFapHubCategory
import com.flipperdevices.faphub.dao.api.model.FapCategory
import com.flipperdevices.faphub.fapscreen.api.FapScreenApi
import com.flipperdevices.faphub.installation.api.FapInstallationUIApi
import com.flipperdevices.faphub.search.api.FapHubSearchEntryApi
import com.squareup.anvil.annotations.ContributesBinding
import com.squareup.anvil.annotations.ContributesMultibinding
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

internal const val CATEGORY_OPEN_PATH_KEY = "open"

@ContributesBinding(AppGraph::class, FapHubCategoryApi::class)
@ContributesMultibinding(AppGraph::class, ComposableFeatureEntry::class)
class FapHubCategoryApiImpl @Inject constructor(
    private val searchEntryApi: FapHubSearchEntryApi,
    private val fapScreenApi: FapScreenApi,
    private val fapInstallationUIApi: FapInstallationUIApi
) : FapHubCategoryApi {
    private val categoryArguments = listOf(
        navArgument(CATEGORY_OPEN_PATH_KEY) {
            type = FapCategoryContentType()
            nullable = false
        }
    )

    override fun open(
        category: FapCategory
    ) = "@${ROUTE.name}?category=${Uri.encode(Json.encodeToString(category))}"

    override fun NavGraphBuilder.composable(navController: NavHostController) {
        composable(
            route = "@${ROUTE.name}?category={$CATEGORY_OPEN_PATH_KEY}",
            categoryArguments
        ) {
            ComposableFapHubCategory(
                onBack = navController::popBackStack,
                onOpenSearch = { navController.navigate(searchEntryApi.start()) },
                onOpenFapItem = {
                    navController.navigate(fapScreenApi.getFapScreen(it.id))
                },
                installationButton = { fapItem, modifier, fontSize ->
                    fapInstallationUIApi.ComposableInstallButton(fapItem, modifier, fontSize)
                }
            )
        }
    }
}
