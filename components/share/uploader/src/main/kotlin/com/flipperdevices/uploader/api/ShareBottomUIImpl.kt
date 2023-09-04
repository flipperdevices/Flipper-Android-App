package com.flipperdevices.uploader.api

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.flipperdevices.bridge.dao.api.delegates.key.SimpleKeyApi
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.keyparser.api.KeyParser
import com.flipperdevices.metric.api.MetricApi
import com.flipperdevices.share.api.CryptoStorageApi
import com.flipperdevices.share.api.ShareBottomUIApi
import com.flipperdevices.uploader.compose.ComposableSheetContent
import com.flipperdevices.uploader.viewmodel.UploaderViewModel
import com.flipperdevices.uploader.viewmodel.UploaderViewModelFactory
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.launch
import javax.inject.Inject

@ContributesBinding(AppGraph::class, ShareBottomUIApi::class)
class ShareBottomUIImpl @Inject constructor(
    private val keyParser: KeyParser,
    private val cryptoStorageApi: CryptoStorageApi,
    private val simpleKeyApi: SimpleKeyApi,
    private val metricApi: MetricApi,
) : ShareBottomUIApi {
    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    override fun ComposableShareBottomSheet(
        flipperKeyPath: FlipperKeyPath,
        screenContent: @Composable (() -> Unit) -> Unit
    ) {
        val viewModel: UploaderViewModel = viewModel(
            factory = UploaderViewModelFactory(
                keyParser = keyParser,
                cryptoStorageApi = cryptoStorageApi,
                simpleKeyApi = simpleKeyApi,
                metricApi = metricApi,
                flipperKeyPath = flipperKeyPath
            )
        )

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

        BackHandler(enabled = sheetState.isVisible) {
            sheetScope.launch { sheetState.hide() }
        }

        ProcessSystemBar(sheetState)
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
                viewModel.invalidate()
                sheetScope.launch { sheetState.show() }
            }
        }
    }

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    private fun ProcessSystemBar(sheetState: ModalBottomSheetState) {
        val systemUIController = rememberSystemUiController()
        key(sheetState.isVisible) {
            if (sheetState.isVisible) {
                systemUIController.setNavigationBarColor(
                    color = LocalPallet.current.shareSheetNavigationBarActiveColor,
                    darkIcons = false
                )
                systemUIController.setStatusBarColor(
                    color = LocalPallet.current.shareSheetStatusBarActiveColor
                )
            } else {
                systemUIController.setNavigationBarColor(
                    color = LocalPallet.current.shareSheetNavigationBarDefaultColor,
                    darkIcons = true
                )
                systemUIController.setStatusBarColor(
                    color = LocalPallet.current.shareSheetStatusBarDefaultColor
                )
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
            state = state,
            keyName = keyName,
            onShareFile = { viewModel.shareByFile(it, context) },
            onShareLink = { viewModel.shareViaLink(it, context) },
            onRetry = viewModel::invalidate,
            onClose = onClose,
        )
    }
}
