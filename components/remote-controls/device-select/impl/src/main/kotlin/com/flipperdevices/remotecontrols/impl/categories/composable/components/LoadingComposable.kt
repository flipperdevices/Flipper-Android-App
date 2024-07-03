package com.flipperdevices.remotecontrols.impl.categories.composable.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.theme.LocalPalletV2

@Composable
internal fun LoadingComposable() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
        content = {
            CircularProgressIndicator(
                color = LocalPalletV2.current.action.brand.background.primary.default,
                modifier = Modifier.size(54.dp)
            )
        }
    )
}
