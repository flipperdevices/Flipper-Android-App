package com.flipperdevices.nfceditor.impl.fragments

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.fragment.app.viewModels
import com.flipperdevices.bridge.dao.api.model.FlipperFile
import com.flipperdevices.bridge.dao.api.model.FlipperFilePath
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.dao.api.model.FlipperKeyContent
import com.flipperdevices.core.ktx.android.withArgs
import com.flipperdevices.core.navigation.delegates.OnBackPressListener
import com.flipperdevices.core.ui.fragment.ComposeFragment
import com.flipperdevices.core.ui.ktx.LocalRouter
import com.flipperdevices.nfceditor.impl.composable.ComposableNfcEditorScreen
import com.flipperdevices.nfceditor.impl.composable.dialog.ComposableNfcEditExitDialog
import com.flipperdevices.nfceditor.impl.viewmodel.NfcEditorViewModel
import com.flipperdevices.nfceditor.impl.viewmodel.NfcEditorViewModelFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

private const val EXTRA_FLIPPER_KEY = "flipper_key"

class NfcEditorFragment : ComposeFragment(), OnBackPressListener {
    private val showOnSaveDialogState = MutableStateFlow(false)
    private val viewModel by viewModels<NfcEditorViewModel> {
        NfcEditorViewModelFactory(
            arguments?.getParcelable(EXTRA_FLIPPER_KEY)
                ?: FlipperKey(
                    mainFile = FlipperFile(
                        FlipperFilePath.DUMMY,
                        FlipperKeyContent.RawData(byteArrayOf())
                    ),
                    synchronized = false,
                    deleted = false
                )
        )
    }

    @Composable
    override fun RenderView() {
        Box(
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background)
        ) {
            val showOnSaveDialog by showOnSaveDialogState.collectAsState()
            if (showOnSaveDialog) {
                val router = LocalRouter.current
                ComposableNfcEditExitDialog(
                    onDismiss = { showOnSaveDialogState.update { false } },
                    onNotSave = { router.exit() },
                    onSave = { viewModel.onSave(router) },
                    onSaveAs = {}
                )
            }

            ComposableNfcEditorScreen(viewModel)
        }
    }

    companion object {
        fun getInstance(flipperKey: FlipperKey) = NfcEditorFragment().withArgs {
            putParcelable(EXTRA_FLIPPER_KEY, flipperKey)
        }
    }

    override fun onBackPressed(): Boolean {
        return if (viewModel.currentActiveCell != null) {
            viewModel.onCellFocus(null)
            true
        } else if (viewModel.isDirty()) {
            showOnSaveDialogState.update { !it }
            true
        } else false
    }
}
