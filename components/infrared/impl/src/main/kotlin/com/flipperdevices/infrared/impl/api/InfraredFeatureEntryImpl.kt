package com.flipperdevices.infrared.impl.api

import android.net.Uri
import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.bridge.dao.api.model.navigation.FlipperKeyPathType
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ui.navigation.ComposableFeatureEntry
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.infrared.api.InfraredFeatureEntry
import com.flipperdevices.infrared.impl.composable.ComposableInfraredScreen
import com.flipperdevices.infrared.impl.viewmodel.InfraredViewModel
import com.flipperdevices.keyedit.api.KeyEditFeatureEntry
import com.flipperdevices.keyemulate.api.KeyEmulateApi
import com.flipperdevices.keyscreen.api.KeyScreenApi
import com.flipperdevices.share.api.ShareBottomUIApi
import com.squareup.anvil.annotations.ContributesBinding
import com.squareup.anvil.annotations.ContributesMultibinding
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import tangle.viewmodel.compose.tangleViewModel
import javax.inject.Inject

internal const val EXTRA_KEY_PATH = "flipper_key_path"

@ContributesBinding(AppGraph::class, InfraredFeatureEntry::class)
@ContributesMultibinding(AppGraph::class, ComposableFeatureEntry::class)
class InfraredFeatureEntryImpl @Inject constructor(
    private val keyScreenApi: KeyScreenApi,
    private val keyEmulateApi: KeyEmulateApi,
    private val keyEditFeatureEntry: KeyEditFeatureEntry,
    private val shareBottomUIApi: ShareBottomUIApi
) : InfraredFeatureEntry {

    override fun getInfraredScreen(keyPath: FlipperKeyPath): String {
        return "@${ROUTE.name}?infrared_key_path=${Uri.encode(Json.encodeToString(keyPath))}"
    }

    private val navArguments = listOf(
        navArgument(EXTRA_KEY_PATH) {
            nullable = false
            type = FlipperKeyPathType()
        }
    )

    override fun NavGraphBuilder.composable(navController: NavHostController) {
        composable(
            route = "@${ROUTE.name}?infrared_key_path={$EXTRA_KEY_PATH}",
            arguments = navArguments
        ) {
            val viewModel: InfraredViewModel = tangleViewModel()

            CompositionLocalProvider(
                LocalPallet provides LocalPallet.current.copy(
                    shareSheetStatusBarColor = LocalPallet.current.accentShareSheetStatusBarColor
                )
            ) {
                shareBottomUIApi.ComposableShareBottomSheet { onShare ->
                    ComposableInfraredScreen(
                        navController = navController,
                        viewModel = viewModel,
                        keyScreenApi = keyScreenApi,
                        keyEmulateApi = keyEmulateApi,
                        onEdit = {},
                        onRename = {
                            navController.navigate(keyEditFeatureEntry.getKeyEditScreen(it, null))
                        },
                        onShare = onShare
                    )
                }
            }
        }
    }
}
