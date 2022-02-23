package com.flipperdevices.keyscreen.impl.fragments

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.fragment.app.viewModels
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.core.ktx.android.withArgs
import com.flipperdevices.core.ui.ComposeFragment
import com.flipperdevices.keyscreen.impl.composable.ComposableKeyScreen
import com.flipperdevices.keyscreen.impl.viewmodel.KeyScreenViewModel
import com.flipperdevices.keyscreen.impl.viewmodel.KeyScreenViewModelFactory

private const val EXTRA_KEY_PATH = "flipper_key_path"

class KeyScreenFragment : ComposeFragment() {
    private val viewModel by viewModels<KeyScreenViewModel> {
        KeyScreenViewModelFactory(
            arguments?.getParcelable(EXTRA_KEY_PATH),
            requireActivity().application
        )
    }

    @Composable
    override fun RenderView() {
        val keyScreenState by viewModel.getKeyScreenState().collectAsState()
        ComposableKeyScreen(viewModel, keyScreenState)
    }

    companion object {
        fun getInstance(keyPath: FlipperKeyPath) = KeyScreenFragment().withArgs {
            putParcelable(EXTRA_KEY_PATH, keyPath)
        }
    }
}
