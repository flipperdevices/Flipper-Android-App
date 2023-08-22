package com.flipperdevices.keyedit.impl.api

import android.net.Uri
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ui.navigation.ComposableFeatureEntry
import com.flipperdevices.keyedit.api.KeyEditFeatureEntry
import com.flipperdevices.keyedit.api.NotSavedFlipperKey
import com.flipperdevices.keyedit.impl.composable.ComposableEditScreen
import com.flipperdevices.keyedit.impl.model.EditableKey
import com.flipperdevices.keyedit.impl.model.navigation.EditableKeyType
import com.flipperdevices.keyedit.impl.viewmodel.KeyEditViewModel
import com.squareup.anvil.annotations.ContributesBinding
import com.squareup.anvil.annotations.ContributesMultibinding
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import tangle.viewmodel.compose.tangleViewModel
import javax.inject.Inject

internal const val EXTRA_EDITABLE_KEY = "editable_key"
internal const val EXTRA_KEY_TITLE = "title"

@ContributesBinding(AppGraph::class, KeyEditFeatureEntry::class)
@ContributesMultibinding(AppGraph::class, ComposableFeatureEntry::class)
class KeyEditFeatureEntryImpl @Inject constructor() : KeyEditFeatureEntry {

    private val keyEditArguments = listOf(
        navArgument(EXTRA_EDITABLE_KEY) {
            nullable = false
            type = EditableKeyType()
        },
        navArgument(EXTRA_KEY_TITLE) {
            nullable = true
            type = NavType.StringType
        }
    )

    override fun getKeyEditScreen(flipperKeyPath: FlipperKeyPath, title: String?): String {
        val editableKey = EditableKey.Existed(flipperKeyPath)
        return createKeyEditScreen(editableKey, title)
    }

    override fun getKeyEditScreen(
        notSavedFlipperKey: NotSavedFlipperKey,
        title: String?
    ): String {
        val editableKey = EditableKey.Limb(notSavedFlipperKey)
        return createKeyEditScreen(editableKey, title)
    }

    private fun createKeyEditScreen(
        editableKey: EditableKey,
        title: String?
    ): String {
        val pathByKey = "@${ROUTE.name}?key_path=${Uri.encode(Json.encodeToString(editableKey))}"
        if (title == null) return pathByKey
        return pathByKey + "&title=${Uri.encode(title)}"
    }

    override fun NavGraphBuilder.composable(navController: NavHostController) {
        composable(
            route = "@${ROUTE.name}?key_path={$EXTRA_EDITABLE_KEY}&title={$EXTRA_KEY_TITLE}",
            arguments = keyEditArguments
        ) {
            val title: String? = it.arguments?.getString(EXTRA_KEY_TITLE)
            val viewModel: KeyEditViewModel = tangleViewModel()
            val state by viewModel.getEditState().collectAsState()
            ComposableEditScreen(
                onNameChange = viewModel::onNameChange,
                onNoteChange = viewModel::onNotesChange,
                title = title,
                state = state,
                onBack = navController::popBackStack,
                onSave = {
                    viewModel.onSave(navController::popBackStack)
                }
            )
        }
    }
}
