package com.flipperdevices.infrared.editor.api

import android.net.Uri
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.bridge.dao.api.model.navigation.FlipperKeyPathType
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ui.navigation.ComposableFeatureEntry
import com.flipperdevices.infrared.api.InfraredEditorFeatureEntry
import com.flipperdevices.infrared.editor.compose.screen.ComposableInfraredEditorScreen
import com.squareup.anvil.annotations.ContributesBinding
import com.squareup.anvil.annotations.ContributesMultibinding
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

internal const val EXTRA_KEY_PATH = "flipper_key_path"

@ContributesBinding(AppGraph::class, InfraredEditorFeatureEntry::class)
@ContributesMultibinding(AppGraph::class, ComposableFeatureEntry::class)
class InfraredFeatureEntryImpl @Inject constructor() : InfraredEditorFeatureEntry {

    override fun getInfraredEditorScreen(keyPath: FlipperKeyPath): String {
        return "@${ROUTE.name}?infrared_editor_key_path=${Uri.encode(Json.encodeToString(keyPath))}"
    }

    private val navArguments = listOf(
        navArgument(EXTRA_KEY_PATH) {
            nullable = false
            type = FlipperKeyPathType()
        }
    )

    override fun NavGraphBuilder.composable(navController: NavHostController) {
        composable(
            route = "@${ROUTE.name}?infrared_editor_key_path={$EXTRA_KEY_PATH}",
            arguments = navArguments
        ) {
            ComposableInfraredEditorScreen(
                onBack = navController::popBackStack
            )
        }
    }
}
