package com.flipperdevices.settings.impl.composable.elements

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.theme.LocalTypography

@Composable
fun Category(
    @StringRes titleId: Int
) {
    Text(
        modifier = Modifier.padding(
            start = 12.dp,
            end = 12.dp,
            top = 48.dp
        ),
        text = stringResource(titleId),
        style = LocalTypography.current.buttonB14
    )
}
