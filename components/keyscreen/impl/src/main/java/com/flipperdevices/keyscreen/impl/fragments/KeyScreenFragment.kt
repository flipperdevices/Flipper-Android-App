package com.flipperdevices.keyscreen.impl.fragments

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.fragment.app.viewModels
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.ktx.android.withArgs
import com.flipperdevices.core.navigation.delegates.OnBackPressListener
import com.flipperdevices.core.ui.ComposeFragment
import com.flipperdevices.keyedit.api.KeyEditApi
import com.flipperdevices.keyscreen.impl.composable.ComposableKeyScreen
import com.flipperdevices.keyscreen.impl.di.KeyScreenComponent
import com.flipperdevices.keyscreen.impl.viewmodel.KeyScreenViewModel
import com.flipperdevices.keyscreen.impl.viewmodel.KeyScreenViewModelFactory
import javax.inject.Inject

private const val EXTRA_KEY_PATH = "flipper_key_path"

class KeyScreenFragment : ComposeFragment(), OnBackPressListener {

    @Inject
    lateinit var keyEditApi: KeyEditApi

    init {
        ComponentHolder.component<KeyScreenComponent>().inject(this)
    }

    private val viewModel by viewModels<KeyScreenViewModel> {
        KeyScreenViewModelFactory(
            arguments?.getParcelable(EXTRA_KEY_PATH),
            requireActivity().application
        )
    }

    @Composable
    override fun RenderView() {
        val keyScreenState by viewModel.getKeyScreenState().collectAsState()
        ComposableKeyScreen(viewModel, keyScreenState, keyEditApi)
    }

    companion object {
        fun getInstance(keyPath: FlipperKeyPath) = KeyScreenFragment().withArgs {
            putParcelable(EXTRA_KEY_PATH, keyPath)
        }
    }

    override fun onBackPressed(): Boolean {
        return viewModel.onBack()
    }
}
