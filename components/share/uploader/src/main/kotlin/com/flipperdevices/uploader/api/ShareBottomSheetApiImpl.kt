package com.flipperdevices.uploader.api

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.share.api.ShareBottomSheetApi
import com.flipperdevices.uploader.compose.ComposableSheetContent
import com.flipperdevices.uploader.viewmodel.UploaderViewModel
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject
import tangle.viewmodel.compose.tangleViewModel

@ContributesBinding(AppGraph::class)
class ShareBottomSheetApiImpl @Inject constructor() : ShareBottomSheetApi {
    @Composable
    override fun ComposableShareBottomSheet(onClose: () -> Unit) {
        val context = LocalContext.current
        val viewModel: UploaderViewModel = tangleViewModel()
        val state = viewModel.getState().collectAsState().value

        val keyName = remember { viewModel.getFlipperKeyName() }
        ComposableSheetContent(
            state = state,
            keyName = keyName,
            onShareFile = { viewModel.shareByFile(it, context) },
            onShareLink = { viewModel.shareViaLink(it, context) },
            onRetry = { viewModel.retryShare() },
            onClose = onClose
        )
    }
}
