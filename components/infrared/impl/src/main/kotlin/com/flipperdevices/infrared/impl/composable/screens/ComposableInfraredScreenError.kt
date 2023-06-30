package com.flipperdevices.infrared.impl.composable.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.flipperdevices.core.ui.ktx.SetUpStatusBarColor
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.keyscreen.model.KeyScreenState

@Composable
internal fun ComposableInfraredScreenError(
    state: KeyScreenState.Error
) {
    SetUpStatusBarColor(color = LocalPallet.current.background, darkIcon = true)
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(state.reason),
            fontWeight = FontWeight.Medium
        )
    }
}
