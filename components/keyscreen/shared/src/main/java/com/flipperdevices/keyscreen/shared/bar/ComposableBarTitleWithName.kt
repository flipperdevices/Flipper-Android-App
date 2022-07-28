package com.flipperdevices.keyscreen.shared.bar

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography

@Composable
fun ComposableBarTitleWithName(modifier: Modifier, title: String, name: String) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            color = LocalPallet.current.text88,
            style = LocalTypography.current.titleEB18
        )
        Text(
            text = name,
            color = LocalPallet.current.text88,
            style = LocalTypography.current.subtitleM12
        )
    }
}

@Composable
fun ComposableBarTitleWithName(modifier: Modifier, @StringRes titleId: Int, name: String) {
    ComposableBarTitleWithName(modifier = modifier, title = stringResource(titleId), name = name)
}
