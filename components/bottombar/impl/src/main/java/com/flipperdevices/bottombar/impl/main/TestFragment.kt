package com.flipperdevices.bottombar.impl.main

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.ComposeFragment

class TestFragment : ComposeFragment() {
    @Composable
    override fun renderView() {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth()
                .background(Color.Red)
                .border(5.dp, Color.Cyan)
        )
    }
}
