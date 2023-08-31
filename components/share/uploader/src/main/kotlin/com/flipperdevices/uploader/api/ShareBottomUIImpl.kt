package com.flipperdevices.uploader.api

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.plusAssign
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.share.api.ShareBottomFeatureEntry
import com.flipperdevices.share.api.ShareBottomUIApi
import com.google.accompanist.navigation.material.rememberBottomSheetNavigator
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

private const val START_SCREEN = "screen"

@ContributesBinding(AppGraph::class, ShareBottomUIApi::class)
class ShareBottomUIImpl @Inject constructor(
    private val shareBottomFeatureEntry: ShareBottomFeatureEntry,
) : ShareBottomUIApi {
    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    override fun ComposableShareBottomSheet(
        flipperKeyPath: FlipperKeyPath,
        screenContent: @Composable (() -> Unit) -> Unit
    ) {
        val scrimColor = if (MaterialTheme.colors.isLight) {
            LocalPallet.current.shareSheetScrimColor
        } else {
            Color.Transparent
        }

        var skipHalfExpanded by remember { mutableStateOf(false) }
        val state = rememberModalBottomSheetState(
            initialValue = ModalBottomSheetValue.Hidden,
            skipHalfExpanded = skipHalfExpanded
        )

        ModalBottomSheetLayout(
            scrimColor = scrimColor,
            sheetBackgroundColor = LocalPallet.current.shareSheetBackground,
            sheetShape = RoundedCornerShape(topEnd = 30.dp, topStart = 30.dp),
            sheetContent = {
                shareBottomFeatureEntry.ShareComposable(path = flipperKeyPath) {
                    skipHalfExpanded = false
                }
            },
            sheetState = state
        ) {
            screenContent { skipHalfExpanded = true }
        }
    }
}
