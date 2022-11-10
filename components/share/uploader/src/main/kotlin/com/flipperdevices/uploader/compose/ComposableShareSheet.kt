package com.flipperdevices.uploader.compose

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetDefaults
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.uploader.models.UploaderState
import com.flipperdevices.uploader.viewmodel.UploaderViewModel

private const val RADIUS = 24

@OptIn(ExperimentalMaterialApi::class)
@Composable
internal fun ComposableShareBottomSheetInternal(
    sheetState: ModalBottomSheetState,
    flipperKey: FlipperKey,
    viewModel: UploaderViewModel,
    state: UploaderState,
    screenContent: @Composable () -> Unit
) {
    val context = LocalContext.current

    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetShape = RoundedCornerShape(topStart = RADIUS.dp, topEnd = RADIUS.dp),
        scrimColor = ModalBottomSheetDefaults.scrimColor.copy(alpha = 0.15f),
        sheetBackgroundColor = LocalPallet.current.shareSheetBackground,
        sheetContent = {
            ComposableSheetContent(
                state = state,
                flipperKey = flipperKey,
                onShareLink = { viewModel.onShareLink(flipperKey, context) },
                onShareFile = { viewModel.onShareFile(flipperKey, context) },
                onRetry = { viewModel.onRetry() }
            )
        }
    ) {
        screenContent()
    }
}
