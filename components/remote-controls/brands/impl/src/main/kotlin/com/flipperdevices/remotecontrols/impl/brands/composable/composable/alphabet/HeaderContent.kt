package com.flipperdevices.remotecontrols.impl.brands.composable.composable.alphabet

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.style.TextAlign
import com.flipperdevices.core.ui.theme.LocalPalletV2
import com.flipperdevices.core.ui.theme.LocalTypography

@Composable
fun HeaderContent(
    isSelected: Boolean,
    text: String,
    onGloballyPositioned: (LayoutCoordinates) -> Unit,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = when (isSelected) {
            true -> 1.5f
            false -> 1f
        }
    )
    val textColor by animateColorAsState(
        targetValue = when {
            isSelected -> LocalPalletV2.current.text.body.primary
            else -> LocalPalletV2.current.text.body.secondary
        }
    )
    Text(
        text = text,
        modifier = modifier
            .onGloballyPositioned(onGloballyPositioned)
            .scale(scale),
        textAlign = TextAlign.Center,
        style = LocalTypography.current.subtitleM12,
        color = textColor
    )
}