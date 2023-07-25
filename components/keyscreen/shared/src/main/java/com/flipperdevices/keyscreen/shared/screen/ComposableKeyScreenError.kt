package com.flipperdevices.keyscreen.shared.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.flipperdevices.core.ui.ktx.SetUpStatusBarColor
import com.flipperdevices.core.ui.theme.LocalPallet

@Composable
fun ComposableKeyScreenError(
    text: String,
    modifier: Modifier = Modifier
) {
    SetUpStatusBarColor(color = LocalPallet.current.background, darkIcon = true)
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontWeight = FontWeight.Medium
        )
    }
}
