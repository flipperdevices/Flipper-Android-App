package com.flipperdevices.keyscreen.emulate.composable.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.theme.LocalPallet

private const val BUTTON_HEIGHT_DP = 49

@Composable
@Suppress("LongParameterList")
fun ComposableEmulateButton(
    modifier: Modifier,
    buttonModifier: Modifier = Modifier,
    buttonContent: (@Composable () -> Unit),
    underButtonContent: (@Composable () -> Unit),
    borderColor: Color? = LocalPallet.current.text8,
    backgroundBrush: Brush = SolidColor(borderColor ?: LocalPallet.current.text8)
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        var buttonLocalModifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .height(BUTTON_HEIGHT_DP.dp)
            .fillMaxWidth()
            .background(backgroundBrush)
        if (borderColor != null) {
            buttonLocalModifier = buttonLocalModifier.border(
                width = 2.dp,
                color = borderColor,
                shape = RoundedCornerShape(12.dp)
            )
        }
        Row(
            modifier = buttonLocalModifier
                .then(buttonModifier)
                .padding(bottom = 4.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            buttonContent()
        }
        underButtonContent()
    }
}
