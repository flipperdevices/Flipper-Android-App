package com.flipper.pair.find

import androidx.compose.runtime.Composable
import com.flipper.core.view.ComposeFragment
import com.flipper.pair.find.compose.ComposeFindDevice

class FindDeviceFragment : ComposeFragment() {
    @Composable
    override fun renderView() {
        ComposeFindDevice()
    }
}