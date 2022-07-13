package com.flipperdevices.info.shared

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.theme.LocalPallet

@Composable
fun ComposableInfoDivider() {
    Divider(
        modifier = Modifier.fillMaxWidth(),
        color = LocalPallet.current.divider12,
        thickness = 1.dp
    )
}
