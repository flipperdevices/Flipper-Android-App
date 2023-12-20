package com.flipperdevices.archive.category.api

import android.net.Uri
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.flipperdevices.archive.api.CategoryFeatureEntry
import com.flipperdevices.archive.category.composable.ComposableCategory
import com.flipperdevices.archive.category.composable.ComposableDeleted
import com.flipperdevices.archive.category.model.CategoryNavType
import com.flipperdevices.archive.model.CategoryType
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.bridge.synchronization.api.SynchronizationUiApi
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ktx.android.parcelable
import com.flipperdevices.core.ui.navigation.ComposableFeatureEntry
import com.squareup.anvil.annotations.ContributesBinding
import com.squareup.anvil.annotations.ContributesMultibinding
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

internal const val EXTRA_CATEGORY_TYPE = "category_type"

@ContributesBinding(AppGraph::class, CategoryFeatureEntry::class)
@ContributesMultibinding(AppGraph::class, ComposableFeatureEntry::class)
class CategoryFeatureEntryImpl @Inject constructor(
    private val synchronizationUiApi: SynchronizationUiApi,
    private val keyScreenFeatureEntry: KeyScreenFeatureEntry
) : CategoryFeatureEntry {
    override fun getCategoryScreen(categoryType: CategoryType): String {
        return "@${ROUTE.name}?type=${Uri.encode(Json.encodeToString(categoryType))}"
    }

    private val categoryArguments = listOf(
        navArgument(EXTRA_CATEGORY_TYPE) {
            nullable = false
            type = CategoryNavType()
        }
    )

    override fun NavGraphBuilder.composable(navController: NavHostController) {
        composable(
            route = "@${ROUTE.name}?type={$EXTRA_CATEGORY_TYPE}",
            arguments = categoryArguments
        ) {
            val onOpenKeyScreen: (FlipperKeyPath) -> Unit = { flipperKeyPath ->
                val keyScreen = keyScreenFeatureEntry.getKeyScreen(flipperKeyPath)
                navController.navigate(keyScreen)
            }

            when (val categoryType = it.arguments?.parcelable<CategoryType>(EXTRA_CATEGORY_TYPE)) {
                null -> {}
                is CategoryType.ByFileType -> ComposableCategory(
                    categoryType = categoryType,
                    synchronizationUiApi = synchronizationUiApi,
                    onBack = navController::popBackStack,
                    onOpenKeyScreen = onOpenKeyScreen,
                )
                CategoryType.Deleted -> ComposableDeleted(
                    onBack = navController::popBackStack,
                    onOpenKeyScreen = onOpenKeyScreen,
                )
            }
        }
    }
}
