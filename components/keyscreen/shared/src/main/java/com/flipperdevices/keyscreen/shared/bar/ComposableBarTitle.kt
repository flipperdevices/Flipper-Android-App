package com.flipperdevices.keyscreen.shared.bar

import androidx.annotation.StringRes
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography

@Composable
fun ComposableBarTitle(modifier: Modifier = Modifier, text: String) {
    Text(
        modifier = modifier,
        text = text,
        color = LocalPallet.current.text88,
        style = LocalTypography.current.titleEB20
    )
}

@Composable
fun ComposableBarTitle(modifier: Modifier = Modifier, @StringRes textId: Int) {
    ComposableBarTitle(modifier = modifier, text = stringResource(textId))
}
