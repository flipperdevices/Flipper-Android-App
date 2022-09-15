package com.flipperdevices.keyedit.impl.fragment

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.fragment.app.viewModels
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.core.ktx.android.withArgs
import com.flipperdevices.core.ui.fragment.ComposeFragment
import com.flipperdevices.core.ui.ktx.LocalRouter
import com.flipperdevices.keyedit.impl.composable.ComposableEditScreen
import com.flipperdevices.keyedit.impl.viewmodel.KeyEditViewModel
import com.flipperdevices.keyedit.impl.viewmodel.KeyEditViewModelFactory

private const val EXTRA_KEY_PATH = "key_path"

class KeyEditFragment : ComposeFragment() {
    private val keyPath: FlipperKeyPath? by lazy {
        arguments?.getParcelable(EXTRA_KEY_PATH)
    }
    private val viewModel by viewModels<KeyEditViewModel> {
        KeyEditViewModelFactory(keyPath, requireContext())
    }

    @Composable
    override fun RenderView() {
        val router = LocalRouter.current
        val state by viewModel.getEditState().collectAsState()
        ComposableEditScreen(
            viewModel,
            state,
            onCancel = router::exit,
            onSave = { viewModel.onSave(router) }
        )
    }

    companion object {
        fun getInstance(keyPath: FlipperKeyPath): KeyEditFragment {
            return KeyEditFragment().withArgs {
                putParcelable(EXTRA_KEY_PATH, keyPath)
            }
        }
    }
}
