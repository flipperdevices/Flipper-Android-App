package com.flipperdevices.nfceditor.impl.fragments

import androidx.compose.runtime.Composable
import com.flipperdevices.core.ui.fragment.ComposeFragment
import com.flipperdevices.nfceditor.impl.composable.ComposableNfcEditor

class NfcEditorFragment : ComposeFragment() {
    @Composable
    override fun RenderView() {
        ComposableNfcEditor()
    }
}
