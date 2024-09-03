package com.flipperdevices.ifrmvp.core.ui.button

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.tooling.preview.Preview
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
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

@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun UnknownButtonPreview() {
    FlipperThemeInternal {
        UnknownButton(
            onClick = {}
        )
    }
}
