package com.flipperdevices.info.impl.fragment

import androidx.compose.runtime.Composable
import com.flipperdevices.core.ui.ComposeFragment
import com.flipperdevices.info.impl.compose.info.ComposableFullDeviceInfoScreen

class FullDeviceInfoFragment : ComposeFragment() {
    @Composable
    override fun RenderView() {
        ComposableFullDeviceInfoScreen()
    }
}
