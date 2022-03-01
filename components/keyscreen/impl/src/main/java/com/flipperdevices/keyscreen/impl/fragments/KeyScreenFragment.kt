package com.flipperdevices.keyscreen.impl.fragments

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.fragment.app.viewModels
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.core.ktx.android.withArgs
import com.flipperdevices.core.navigation.requireRouter
import com.flipperdevices.core.ui.ComposeFragment
import com.flipperdevices.keyscreen.impl.composable.view.ComposableKeyScreen
import com.flipperdevices.keyscreen.impl.viewmodel.view.KeyScreenViewModel
import com.flipperdevices.keyscreen.impl.viewmodel.view.KeyScreenViewModelFactory
import com.github.terrakok.cicerone.androidx.FragmentScreen

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
        ComposableKeyScreen(viewModel, keyScreenState, onOpenEdit = this::openEditScreen)
    }

    private fun openEditScreen() {
        val keyPathNotNull = arguments?.getParcelable<FlipperKeyPath>(EXTRA_KEY_PATH) ?: return

        requireRouter().navigateTo(
            FragmentScreen {
                KeyEditFragment.getInstance(keyPathNotNull)
            }
        )
    }

    companion object {
        fun getInstance(keyPath: FlipperKeyPath) = KeyScreenFragment().withArgs {
            putParcelable(EXTRA_KEY_PATH, keyPath)
        }
    }
}
