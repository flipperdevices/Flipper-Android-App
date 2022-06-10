package com.flipperdevices.info.impl.compose.info

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.res.R as DesignSystem

@Composable
fun ComposableInfoDivider() {
    Divider(
        modifier = Modifier.fillMaxWidth(),
        color = colorResource(DesignSystem.color.black_4),
        thickness = 1.dp
    )
}
