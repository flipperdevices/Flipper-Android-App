package com.flipperdevices.nfceditor.impl.fragments

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.fragment.app.viewModels
import com.flipperdevices.bridge.dao.api.model.FlipperFile
import com.flipperdevices.bridge.dao.api.model.FlipperFilePath
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.dao.api.model.FlipperKeyContent
import com.flipperdevices.core.ktx.android.parcelable
import com.flipperdevices.core.ktx.android.withArgs
import com.flipperdevices.core.navigation.delegates.OnBackPressListener
import com.flipperdevices.core.navigation.requireRouter
import com.flipperdevices.core.ui.fragment.ComposeFragment
import com.flipperdevices.nfceditor.impl.composable.ComposableNfcEditorScreen
import com.flipperdevices.nfceditor.impl.viewmodel.NfcEditorViewModel
import com.flipperdevices.nfceditor.impl.viewmodel.NfcEditorViewModelFactory

private const val EXTRA_FLIPPER_KEY = "flipper_key"

class NfcEditorFragment : ComposeFragment(), OnBackPressListener {
    private val viewModel by viewModels<NfcEditorViewModel> {
        NfcEditorViewModelFactory(
            requireContext().applicationContext as Application,
            arguments?.parcelable(EXTRA_FLIPPER_KEY)
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
            ComposableNfcEditorScreen(viewModel)
        }
    }

    companion object {
        fun getInstance(flipperKey: FlipperKey) = NfcEditorFragment().withArgs {
            putParcelable(EXTRA_FLIPPER_KEY, flipperKey)
        }
    }

    override fun onBackPressed(): Boolean {
        viewModel.onBack(requireRouter())
        return true
    }
}
