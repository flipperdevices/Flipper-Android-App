package com.flipperdevices.info.impl.fragment

import androidx.compose.runtime.Composable
import com.flipperdevices.core.ui.ComposeFragment
import com.flipperdevices.info.impl.compose.ComposableDeviceInfoScreen

class InfoFragment : ComposeFragment() {
    @Composable
    override fun RenderView() {
        ComposableDeviceInfoScreen()
    }
}
