package com.flipperdevices.uploader.api

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.navArgument
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.share.api.ShareBottomFeatureEntry
import com.flipperdevices.uploader.compose.ComposableSheetContent
import com.flipperdevices.uploader.viewmodel.UploaderViewModel
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import tangle.viewmodel.compose.tangleViewModel

internal const val EXTRA_KEY_PATH = "flipper_key_path"

@ContributesBinding(AppGraph::class, ShareBottomFeatureEntry::class)
class ShareBottomSheetFeatureEntryImpl @Inject constructor() : ShareBottomFeatureEntry {
    override val featureRoute = "@$ROUTE?keyPath={$EXTRA_KEY_PATH}"

    override val arguments: List<NamedNavArgument>
        get() = listOf(
            navArgument(EXTRA_KEY_PATH) {
                type = FlipperKeyPathType()
                nullable = true
            }
        )

    override fun shareDestination(
        path: FlipperKeyPath?
    ) = "@$ROUTE?keyPath=${Uri.encode(Json.encodeToString(path))}"

    @Composable
    override fun NavGraphBuilder.Composable(
        navController: NavHostController,
        backStackEntry: NavBackStackEntry
    ) {
        val context = LocalContext.current
        val viewModel: UploaderViewModel = tangleViewModel()
        val state = viewModel.getState().collectAsState().value

        val keyName = remember(viewModel::getFlipperKeyName)
        ComposableSheetContent(
            state = state,
            keyName = keyName,
            onShareFile = { viewModel.shareByFile(it, context) },
            onShareLink = { viewModel.shareViaLink(it, context) },
            onRetry = viewModel::retryShare,
            onClose = navController::popBackStack
        )
    }
}
