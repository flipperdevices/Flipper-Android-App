package com.flipperdevices.bottombar.impl.main.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.flipperdevices.bottombar.impl.model.FlipperBottomTab

@Preview(
    showBackground = true
)
@Composable
fun ComposeBottomBar(
    selectedItem: FlipperBottomTab = FlipperBottomTab.STORAGE,
    onBottomBarClick: (FlipperBottomTab) -> Unit = {}
) {
}
