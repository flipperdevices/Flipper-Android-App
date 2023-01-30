package com.flipperdevices.keyedit.impl.api

import android.net.Uri
import android.os.Bundle
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ktx.android.parcelable
import com.flipperdevices.core.ui.navigation.ComposableFeatureEntry
import com.flipperdevices.keyedit.api.KeyEditFeatureEntry
import com.flipperdevices.keyedit.impl.composable.ComposableEditScreen
import com.flipperdevices.keyedit.impl.viewmodel.KeyEditViewModel
import com.squareup.anvil.annotations.ContributesBinding
import com.squareup.anvil.annotations.ContributesMultibinding
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import tangle.viewmodel.compose.tangleViewModel
import javax.inject.Inject

internal const val EXTRA_KEY_PATH = "flipper_key_path"
internal const val EXTRA_KEY_TITLE = "title"

@ContributesBinding(AppGraph::class, KeyEditFeatureEntry::class)
@ContributesMultibinding(AppGraph::class, ComposableFeatureEntry::class)
class KeyEditFeatureEntryImpl @Inject constructor() : KeyEditFeatureEntry {

    private val keyEditArguments = listOf(
        navArgument(EXTRA_KEY_PATH) {
            nullable = false
            type = FlipperKeyPathType()
        },
        navArgument(EXTRA_KEY_TITLE) {
            nullable = true
            type = NavType.StringType
        }
    )

    override fun getKeyEditScreen(flipperKeyPath: FlipperKeyPath, title: String?): String {
        val pathByKey = "@${ROUTE.name}?key_path=${Uri.encode(Json.encodeToString(flipperKeyPath))}"
        if (title == null) return pathByKey
        return pathByKey + "&title=${Uri.encode(title)}"
    }

    override fun NavGraphBuilder.composable(navController: NavHostController) {
        composable(
            route = "@${ROUTE.name}?key_path={$EXTRA_KEY_PATH}&title={$EXTRA_KEY_TITLE}",
            arguments = keyEditArguments
        ) {
            val title: String? = it.arguments?.getString(EXTRA_KEY_TITLE)
            val viewModel: KeyEditViewModel = tangleViewModel()
            val state by viewModel.getEditState().collectAsState()
            ComposableEditScreen(
                viewModel,
                title = title,
                state = state,
                onCancel = {
                    navController.popBackStack()
                },
                onSave = {
                    viewModel.onSave { navController.popBackStack() }
                }
            )
        }
    }
}

class FlipperKeyPathType : NavType<FlipperKeyPath>(isNullableAllowed = false) {
    override fun get(bundle: Bundle, key: String): FlipperKeyPath? {
        return bundle.parcelable(key)
    }

    override fun parseValue(value: String): FlipperKeyPath {
        return Json.decodeFromString(value)
    }

    override fun put(bundle: Bundle, key: String, value: FlipperKeyPath) {
        bundle.putParcelable(key, value)
    }
}
