package com.flipperdevices.ifrmvp.core.ui.button

import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import com.flipperdevices.core.ui.theme.LocalPalletV2
import com.flipperdevices.ifrmvp.core.ui.button.core.SquareIconButton

@Composable
fun UnknownButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    SquareIconButton(
        onClick = onClick,
        background = LocalPalletV2.current.action.danger.background.primary.default,
        painter = rememberVectorPainter(Icons.Default.Error),
        iconTint = MaterialTheme.colors.onPrimary,
        modifier = modifier,
    )
}
