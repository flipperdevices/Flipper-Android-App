package com.flipperdevices.keyscreen.impl.fragments

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.fragment.app.viewModels
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.core.ktx.android.withArgs
import com.flipperdevices.core.ui.ComposeFragment
import com.flipperdevices.keyscreen.impl.composable.ComposableKeyScreen
import com.flipperdevices.keyscreen.impl.viewmodel.KeyScreenViewModel
import com.flipperdevices.keyscreen.impl.viewmodel.KeyScreenViewModelFactory

private const val EXTRA_KEY_PATH = "flipper_key_path"

class KeyScreenFragment : ComposeFragment() {
    private val viewModel by viewModels<KeyScreenViewModel>() {
        KeyScreenViewModelFactory(arguments!!.getParcelable(EXTRA_KEY_PATH)!!)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.loadKeyContent()
    }

    @Composable
    override fun RenderView() {
        ComposableKeyScreen()
    }

    companion object {
        fun getInstance(keyPath: FlipperKeyPath) = KeyScreenFragment().withArgs {
            putParcelable(EXTRA_KEY_PATH, keyPath)
        }
    }
}
