package com.flipperdevices.wearable.setup.impl.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.wear.compose.material.Text
import com.flipperdevices.core.ui.theme.LocalTypography

@Composable
fun ComposableFindPhone() {
    Box(contentAlignment = Alignment.Center) {
        Text(
            modifier = Modifier.background(Color.Red),
            text = "Hello!",
            style = LocalTypography.current.titleB18
        )
    }
}
