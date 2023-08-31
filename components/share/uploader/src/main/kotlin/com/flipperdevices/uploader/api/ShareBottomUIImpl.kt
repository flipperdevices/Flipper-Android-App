package com.flipperdevices.uploader.api

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.share.api.ShareBottomUIApi
import com.flipperdevices.uploader.compose.ComposableSheetContent
import com.flipperdevices.uploader.viewmodel.UploaderViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.launch
import tangle.viewmodel.compose.tangleViewModel
import javax.inject.Inject

@ContributesBinding(AppGraph::class, ShareBottomUIApi::class)
class ShareBottomUIImpl @Inject constructor() : ShareBottomUIApi {
    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    override fun ComposableShareBottomSheet(
        screenContent: @Composable (() -> Unit) -> Unit
    ) {
        val viewModel: UploaderViewModel = tangleViewModel()

        val systemUIController = rememberSystemUiController()
        val scrimColor = if (MaterialTheme.colors.isLight) {
            LocalPallet.current.shareSheetScrimColor
        } else {
            Color.Transparent
        }

        val sheetState = rememberModalBottomSheetState(
            initialValue = ModalBottomSheetValue.Hidden,
            skipHalfExpanded = true
        )
        key(sheetState) {
            if (sheetState.isVisible) {
                systemUIController.setNavigationBarColor(
                    color = LocalPallet.current.shareSheetNavigationBarActiveColor,
                    darkIcons = false
                )
                systemUIController.setStatusBarColor(
                    color = LocalPallet.current.shareSheetStatusBarActiveColor
                )
                viewModel.invalidate()
            } else {
                systemUIController.setNavigationBarColor(
                    color = LocalPallet.current.shareSheetNavigationBarDefaultColor,
                    darkIcons = true
                )
                systemUIController.setStatusBarColor(
                    color = LocalPallet.current.shareSheetStatusBarDefaultColor
                )
                viewModel.resetState()
            }
        }

        val sheetScope = rememberCoroutineScope()

        BackHandler(enabled = sheetState.isVisible) {
            sheetScope.launch { sheetState.hide() }
        }

        ModalBottomSheetLayout(
            scrimColor = scrimColor,
            sheetBackgroundColor = LocalPallet.current.shareSheetBackground,
            sheetShape = RoundedCornerShape(topEnd = 30.dp, topStart = 30.dp),
            sheetContent = {
                ComposableShareBottomSheetInternal(viewModel) {
                    sheetScope.launch { sheetState.hide() }
                }
            },
            sheetState = sheetState
        ) {
            screenContent {
                sheetScope.launch { sheetState.show() }
            }
        }
    }

    @Composable
    private fun ComposableShareBottomSheetInternal(
        viewModel: UploaderViewModel,
        onClose: () -> Unit
    ) {
        val context = LocalContext.current

        val state by viewModel.getState().collectAsState()
        key(state) {
            Log.i("ShareBottomUIImpl", "$state")
        }
        val keyName = remember(viewModel::getFlipperKeyName)

        ComposableSheetContent(
            state = state,
            keyName = keyName,
            onShareFile = { viewModel.shareByFile(it, context) },
            onShareLink = { viewModel.shareViaLink(it, context) },
            onRetry = viewModel::invalidate,
            onClose = onClose,
        )
    }
}
