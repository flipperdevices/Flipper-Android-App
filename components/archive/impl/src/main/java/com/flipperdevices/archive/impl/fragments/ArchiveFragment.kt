package com.flipperdevices.archive.impl.fragments

import androidx.compose.runtime.Composable
import com.flipperdevices.archive.impl.composable.ComposableArchive
import com.flipperdevices.core.ui.ComposeFragment

class ArchiveFragment : ComposeFragment() {
    @Composable
    override fun RenderView() {
        ComposableArchive()
    }
}
