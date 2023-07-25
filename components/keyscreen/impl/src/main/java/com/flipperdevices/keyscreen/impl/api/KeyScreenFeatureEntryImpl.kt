package com.flipperdevices.keyscreen.impl.api

import android.net.Uri
import androidx.datastore.core.DataStore
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.bridge.dao.api.model.FlipperKeyType
import com.flipperdevices.bridge.dao.api.model.navigation.FlipperKeyPathType
import com.flipperdevices.bridge.synchronization.api.SynchronizationUiApi
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.preference.pb.Settings
import com.flipperdevices.core.ui.navigation.ComposableFeatureEntry
import com.flipperdevices.core.ui.navigation.LocalGlobalNavigationNavStack
import com.flipperdevices.deeplink.model.DeeplinkConstants
import com.flipperdevices.infrared.api.InfraredFeatureEntry
import com.flipperdevices.keyedit.api.KeyEditFeatureEntry
import com.flipperdevices.keyemulate.api.KeyEmulateApi
import com.flipperdevices.keyscreen.api.KeyScreenFeatureEntry
import com.flipperdevices.keyscreen.impl.composable.ComposableKeyScreen
import com.flipperdevices.keyscreen.impl.viewmodel.KeyScreenViewModel
import com.flipperdevices.nfceditor.api.NfcEditorApi
import com.flipperdevices.nfceditor.api.NfcEditorFeatureEntry
import com.flipperdevices.share.api.ShareBottomUIApi
import com.squareup.anvil.annotations.ContributesBinding
import com.squareup.anvil.annotations.ContributesMultibinding
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
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
    private val shareBottomApi: ShareBottomUIApi,
    private val infraredFeatureEntry: InfraredFeatureEntry,
    private val dataStoreSettings: DataStore<Settings>,
) : KeyScreenFeatureEntry {
    override fun getKeyScreen(keyPath: FlipperKeyPath): String {
        val defaultPath = "@${ROUTE.name}?key_path=${Uri.encode(Json.encodeToString(keyPath))}"

        if (isOpenNewInfraredScreen(keyPath)) {
            return infraredFeatureEntry.getInfraredScreen(keyPath)
        }

        return defaultPath
    }

    private fun isOpenNewInfraredScreen(keyPath: FlipperKeyPath): Boolean {
        val settings = runBlocking { dataStoreSettings.data.first() }
        val isNewInfraredEnable = settings.useNewInfrared

        return when {
            keyPath.path.keyType != FlipperKeyType.INFRARED -> false
            keyPath.deleted -> false
            else -> isNewInfraredEnable
        }
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
            shareBottomApi.ComposableShareBottomSheet { onShare ->
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
