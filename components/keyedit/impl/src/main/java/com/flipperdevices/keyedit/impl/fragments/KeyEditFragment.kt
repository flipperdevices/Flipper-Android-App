package com.flipperdevices.keyedit.impl.fragments

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.fragment.app.viewModels
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.core.ktx.android.withArgs
import com.flipperdevices.core.navigation.requireRouter
import com.flipperdevices.core.ui.ComposeFragment
import com.flipperdevices.keyedit.impl.composable.ComposableEditScreen

private const val EXTRA_KEY_PATH = "flipper_key_path"

class KeyEditFragment : ComposeFragment() {
    private val viewModel by viewModels<com.flipperdevices.keyedit.impl.viewmodel.KeyEditViewModel> {
        com.flipperdevices.keyedit.impl.viewmodel.KeyEditViewModelFactory(
            arguments?.getParcelable(EXTRA_KEY_PATH)!!,
            requireActivity().application
        )
    }

    @Composable
    override fun RenderView() {
        val state by viewModel.getEditState().collectAsState()
        if (state is com.flipperdevices.keyedit.impl.model.KeyEditState.Finished) {
            close()
            return
        }

        ComposableEditScreen(viewModel, state)
    }

    private fun close() {
        requireRouter().exit()
    }

    companion object {
        fun getInstance(keyPath: FlipperKeyPath) = KeyEditFragment().withArgs {
            putParcelable(EXTRA_KEY_PATH, keyPath)
        }
    }
}
