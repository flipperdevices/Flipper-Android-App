package com.flipperdevices.nfceditor.impl.fragments

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.flipperdevices.core.ui.fragment.ComposeFragment
import com.flipperdevices.nfceditor.impl.composable.ComposableNfcEditor

class NfcEditorFragment : ComposeFragment() {
    @Composable
    override fun RenderView() {
        Box(
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background)
        ) {
            ComposableNfcEditor()
        }
    }
}
