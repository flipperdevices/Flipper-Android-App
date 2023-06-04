package com.flipperdevices.keyscreen.impl.api

import android.net.Uri
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.bridge.dao.api.model.navigation.FlipperKeyPathType
import com.flipperdevices.bridge.synchronization.api.SynchronizationUiApi
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ui.navigation.ComposableFeatureEntry
import com.flipperdevices.core.ui.navigation.LocalGlobalNavigationNavStack
import com.flipperdevices.deeplink.model.DeeplinkConstants
import com.flipperdevices.keyedit.api.KeyEditFeatureEntry
import com.flipperdevices.keyemulate.api.KeyEmulateApi
import com.flipperdevices.keyscreen.api.KeyScreenFeatureEntry
import com.flipperdevices.keyscreen.impl.composable.ComposableKeyScreen
import com.flipperdevices.keyscreen.impl.composable.card.KeyScreenNavigation
import com.flipperdevices.keyscreen.impl.viewmodel.KeyScreenViewModel
import com.flipperdevices.nfceditor.api.NfcEditorApi
import com.flipperdevices.nfceditor.api.NfcEditorFeatureEntry
import com.flipperdevices.share.api.ShareBottomFeatureEntry
import com.squareup.anvil.annotations.ContributesBinding
import com.squareup.anvil.annotations.ContributesMultibinding
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import tangle.viewmodel.compose.tangleViewModel
import javax.inject.Inject

internal const val EXTRA_KEY_PATH = "flipper_key_path"
private const val DEEPLINK_SCHEME = DeeplinkConstants.SCHEMA
private const val DEEPLINK_FLIPPER_KEY_URL = "${DEEPLINK_SCHEME}flipper_key={$EXTRA_KEY_PATH}"

@Suppress("LongParameterList")
@ContributesBinding(AppGraph::class, KeyScreenFeatureEntry::class)
@ContributesMultibinding(AppGraph::class, ComposableFeatureEntry::class)
class KeyScreenFeatureEntryImpl @Inject constructor(
    private val synchronizationUiApi: SynchronizationUiApi,
    private val nfcEditor: NfcEditorApi,
    private val keyEmulateApi: KeyEmulateApi,
    private val nfcEditorFeatureEntry: NfcEditorFeatureEntry,
    private val keyEditFeatureEntry: KeyEditFeatureEntry,
    private val shareBottomFeatureEntry: ShareBottomFeatureEntry
) : KeyScreenFeatureEntry {
    override fun getKeyScreen(keyPath: FlipperKeyPath): String {
        return "@${ROUTE.name}?key_path=${Uri.encode(Json.encodeToString(keyPath))}"
    }

    override fun getKeyScreenByDeeplink(keyPath: FlipperKeyPath): String {
        val keyPathStr = Uri.encode(Json.encodeToString(keyPath))
        return "${DEEPLINK_SCHEME}flipper_key=$keyPathStr"
    }

    private val keyScreenArguments = listOf(
        navArgument(EXTRA_KEY_PATH) {
            nullable = false
            type = FlipperKeyPathType()
        }
    )

    private val deeplinkArguments = listOf(
        navDeepLink {
            uriPattern = DEEPLINK_FLIPPER_KEY_URL
        }
    )

    override fun NavGraphBuilder.composable(navController: NavHostController) {
        composable(
            route = "@${ROUTE.name}?key_path={$EXTRA_KEY_PATH}",
            arguments = keyScreenArguments,
            deepLinks = deeplinkArguments
        ) {
            val viewModel: KeyScreenViewModel = tangleViewModel()
            val globalNavController = LocalGlobalNavigationNavStack.current
            KeyScreenNavigation(shareBottomFeatureEntry) { onShare ->
                ComposableKeyScreen(
                    viewModel = viewModel,
                    synchronizationUiApi = synchronizationUiApi,
                    nfcEditorApi = nfcEditor,
                    keyEmulateApi = keyEmulateApi,
                    onShare = onShare,
                    onBack = navController::popBackStack,
                    onOpenNfcEditor = {
                        viewModel.openNfcEditor { flipperKeyPath ->
                            val nfcEditorScreen =
                                nfcEditorFeatureEntry.getNfcEditorScreen(flipperKeyPath)
                            globalNavController.navigate(nfcEditorScreen)
                        }
                    },
                    onOpenEditScreen = { flipperKeyPath ->
                        val keyEditScreen =
                            keyEditFeatureEntry.getKeyEditScreen(flipperKeyPath, null)
                        navController.navigate(keyEditScreen)
                    },
                )
            }
        }
    }
}
