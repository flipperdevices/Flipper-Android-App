package com.flipperdevices.uploader.api

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ui.lifecycle.viewModelWithFactory
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.share.api.ShareBottomUIApi
import com.flipperdevices.uploader.compose.ComposableSheetContent
import com.flipperdevices.uploader.viewmodel.UploaderViewModel
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.launch
import javax.inject.Inject

@ContributesBinding(AppGraph::class, ShareBottomUIApi::class)
class ShareBottomUIImpl @Inject constructor(
    private val uploaderViewModelFactory: UploaderViewModel.Factory
) : ShareBottomUIApi {
    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    @Suppress("NonSkippableComposable")
    override fun ComposableShareBottomSheet(
        provideFlipperKeyPath: () -> FlipperKeyPath,
        componentContext: ComponentContext,
        onSheetStateVisible: @Composable (isVisible: Boolean, onClose: () -> Unit) -> Unit,
        screenContent: @Composable (() -> Unit) -> Unit,
    ) {
        val viewModel = componentContext.viewModelWithFactory(provideFlipperKeyPath.invoke()) {
            uploaderViewModelFactory(provideFlipperKeyPath)
        }

        val scrimColor = if (MaterialTheme.colors.isLight) {
            LocalPallet.current.shareSheetScrimColor
        } else {
            Color.Transparent
        }

        val sheetState = rememberModalBottomSheetState(
            initialValue = ModalBottomSheetValue.Hidden,
            skipHalfExpanded = true
        )

        val sheetScope = rememberCoroutineScope()

        onSheetStateVisible(sheetState.isVisible) {
            sheetScope.launch { sheetState.hide() }
        }

        // ProcessSystemBar(sheetState)
        ModalBottomSheetLayout(
            scrimColor = scrimColor,
            sheetBackgroundColor = LocalPallet.current.shareSheetBackground,
            sheetShape = RoundedCornerShape(topEnd = 30.dp, topStart = 30.dp),
            sheetContent = {
                ComposableShareBottomSheetInternal(viewModel) {
                    sheetScope.launch { sheetState.hide() }
                }
            },
            sheetState = sheetState,
        ) {
            screenContent {
                viewModel.invalidate()
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
        val keyName = remember(viewModel::getFlipperKeyName)

        ComposableSheetContent(
            modifier = Modifier
                .background(LocalPallet.current.shareSheetBackground)
                .navigationBarsPadding(),
            state = state,
            keyName = keyName,
            onShareFile = { viewModel.shareByFile(it, context) },
            onShareLink = { viewModel.shareViaLink(it, context) },
            onRetry = viewModel::invalidate,
            onClose = onClose,
        )
    }
}
