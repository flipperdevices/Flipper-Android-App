package com.flipperdevices.infrared.api

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.flipperdevices.bridge.dao.api.model.navigation.FlipperKeyPathType
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ui.navigation.AggregateFeatureEntry
import com.flipperdevices.infrared.compose.ComposableInfraredScreen
import com.flipperdevices.infrared.viewmodel.InfraredViewModel
import com.flipperdevices.keyedit.api.KeyEditFeatureEntry
import com.flipperdevices.keyscreen.api.KeyEmulateApi
import com.flipperdevices.share.api.ShareBottomUIApi
import com.squareup.anvil.annotations.ContributesBinding
import com.squareup.anvil.annotations.ContributesMultibinding
import tangle.viewmodel.compose.tangleViewModel
import javax.inject.Inject

internal const val EXTRA_KEY_PATH = "flipper_key_path"

@ContributesBinding(AppGraph::class, InfraredFeatureEntry::class)
@ContributesMultibinding(AppGraph::class, AggregateFeatureEntry::class)
class InfraredFeatureEntryImpl @Inject constructor(
    private val shareBottomUI: ShareBottomUIApi,
    private val keyEditFeatureEntry: KeyEditFeatureEntry,
    private val keyEmulateApi: KeyEmulateApi,
    private val infraredEditorFeatureEntry: InfraredEditorFeatureEntry
) : InfraredFeatureEntry {

    private val keyScreenArguments = listOf(
        navArgument(EXTRA_KEY_PATH) {
            nullable = false
            type = FlipperKeyPathType()
        }
    )

    override fun NavGraphBuilder.navigation(navController: NavHostController) {
        composable(
            route = "@${ROUTE.name}?key_path={$EXTRA_KEY_PATH}",
            arguments = keyScreenArguments,
        ) {
            val viewModel: InfraredViewModel = tangleViewModel()

            shareBottomUI.ComposableShareBottomSheet { onShare ->
                ComposableInfraredScreen(
                    viewModel = viewModel,
                    onBack = navController::popBackStack,
                    keyEmulateApi = keyEmulateApi,
                    onRename = {
                        val keyEditScreen = keyEditFeatureEntry.getKeyEditScreen(it, null)
                        navController.navigate(keyEditScreen)
                    },
                    onEdit = {
                        val infraredEditorScreen = infraredEditorFeatureEntry.getEditorScreen(it)
                        navController.navigate(infraredEditorScreen)
                    },
                    onShare = onShare
                )
            }
        }
    }
}
