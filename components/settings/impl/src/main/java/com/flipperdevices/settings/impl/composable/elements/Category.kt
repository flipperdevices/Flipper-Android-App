package com.flipperdevices.settings.impl.composable.elements

import androidx.annotation.StringRes
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

@Composable
fun Category(
    @StringRes titleId: Int
) {
    Text(
        text = stringResource(titleId)
    )
}
