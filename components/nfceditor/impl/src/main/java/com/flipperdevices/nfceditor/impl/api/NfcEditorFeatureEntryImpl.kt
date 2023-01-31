package com.flipperdevices.nfceditor.impl.api

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.bridge.dao.api.model.navigation.FlipperKeyPathType
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ui.navigation.ComposableFeatureEntry
import com.flipperdevices.keyedit.api.KeyEditFeatureEntry
import com.flipperdevices.nfceditor.api.NfcEditorFeatureEntry
import com.flipperdevices.nfceditor.impl.R
import com.flipperdevices.nfceditor.impl.composable.ComposableNfcEditorScreen
import com.squareup.anvil.annotations.ContributesBinding
import com.squareup.anvil.annotations.ContributesMultibinding
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

internal const val EXTRA_KEY_PATH = "flipper_key_path"

@ContributesBinding(AppGraph::class, NfcEditorFeatureEntry::class)
@ContributesMultibinding(AppGraph::class, ComposableFeatureEntry::class)
class NfcEditorFeatureEntryImpl @Inject constructor(
    private val keyEditFeatureEntry: KeyEditFeatureEntry
) : NfcEditorFeatureEntry {
    override fun getNfcEditorScreen(flipperKeyPath: FlipperKeyPath): String {
        return "@${ROUTE.name}?key_path=${Uri.encode(Json.encodeToString(flipperKeyPath))}"
    }

    private val nfcEditorArguments = listOf(
        navArgument(EXTRA_KEY_PATH) {
            nullable = false
            type = FlipperKeyPathType()
        }
    )

    override fun NavGraphBuilder.composable(navController: NavHostController) {
        composable(
            route = "@${ROUTE.name}?key_path={$EXTRA_KEY_PATH}",
            arguments = nfcEditorArguments
        ) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colors.background)
            ) {
                val saveAsTitle = LocalContext.current.getString(R.string.nfc_dialog_save_as_title)
                ComposableNfcEditorScreen(
                    onBack = navController::popBackStack,
                    onSaveEndAction = navController::popBackStack,
                    onSaveAsEndAction = { notSavedFlipperKey ->
                        val keyEditScreen =
                            keyEditFeatureEntry.getKeyEditScreen(notSavedFlipperKey, saveAsTitle)
                        navController.navigate(keyEditScreen)
                    }
                )
            }
        }
    }
}
