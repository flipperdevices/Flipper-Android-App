package com.flipperdevices.core.ui.dialog.composable.multichoice

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.ktx.clickableRipple
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography

@Composable
internal fun ComposableFlipperFlatButton(
    text: String,
    onClick: () -> Unit,
    textColor: Color? = null
) {
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .clickableRipple(onClick = onClick)
            .padding(vertical = 14.dp),
        text = text,
        textAlign = TextAlign.Center,
        color = textColor ?: LocalPallet.current.text100,
        style = LocalTypography.current.bodyM14
    )
}
