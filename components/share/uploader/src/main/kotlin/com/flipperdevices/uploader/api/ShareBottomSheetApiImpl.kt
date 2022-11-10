package com.flipperdevices.uploader.api

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.share.api.ShareBottomSheetApi
import com.flipperdevices.uploader.compose.ComposableShareBottomSheetInternal
import com.flipperdevices.uploader.viewmodel.UploaderViewModel
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject
import tangle.viewmodel.compose.tangleViewModel

@ContributesBinding(AppGraph::class)
class ShareBottomSheetApiImpl @Inject constructor() : ShareBottomSheetApi {
    @OptIn(ExperimentalMaterialApi::class)
    private var bottomSheetScaffoldState: ModalBottomSheetState? = null

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    override fun ComposableShareBottomSheet(
        flipperKey: FlipperKey,
        modifier: Modifier,
        screenContent: @Composable () -> Unit
    ) {
        val viewModel = tangleViewModel<UploaderViewModel>()
        val state = viewModel.getState().collectAsState().value

        bottomSheetScaffoldState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
        var isSheetOpened by remember { mutableStateOf(false) }
        LaunchedEffect(key1 = bottomSheetScaffoldState?.currentValue) {
            isSheetOpened = when (bottomSheetScaffoldState?.currentValue) {
                ModalBottomSheetValue.Expanded -> true
                else -> false
            }
        }

        bottomSheetScaffoldState?.let { sheetState ->
            ComposableShareBottomSheetInternal(
                flipperKey = flipperKey,
                viewModel = viewModel,
                state = state,
                sheetState = sheetState,
                screenContent = screenContent
            )
        }

//        TODO after rework navigation on compose
//        BackHandler {
//            coroutineScope.launch {
//                if (isSheetOpened) bottomSheetScaffoldState?.hide()
//            }
//        }
    }

    @OptIn(ExperimentalMaterialApi::class)
    override suspend fun showSheet() {
        bottomSheetScaffoldState?.show()
    }
}
