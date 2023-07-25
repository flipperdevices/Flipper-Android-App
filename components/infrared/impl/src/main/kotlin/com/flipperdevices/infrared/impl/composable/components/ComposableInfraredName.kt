package com.flipperdevices.infrared.impl.composable.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography

@Composable
internal fun ComposableInfraredName(
    keyName: String
) {
    Text(
        modifier = Modifier.padding(vertical = 14.dp),
        text = keyName,
        textAlign = TextAlign.Center,
        style = LocalTypography.current.buttonB16.copy(
            color = LocalPallet.current.text100
        )
    )
}
