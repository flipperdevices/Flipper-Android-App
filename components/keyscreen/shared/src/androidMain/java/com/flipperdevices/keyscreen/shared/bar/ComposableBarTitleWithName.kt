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
fun ComposableBarTitleWithName(
    title: String,
    name: String?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            color = LocalPallet.current.text88,
            style = LocalTypography.current.titleEB18
        )
        name?.let {
            Text(
                text = it,
                color = LocalPallet.current.text88,
                style = LocalTypography.current.subtitleM12
            )
        }
    }
}

@Composable
fun ComposableBarTitleWithName(
    @StringRes titleId: Int,
    name: String,
    modifier: Modifier = Modifier
) {
    ComposableBarTitleWithName(modifier = modifier, title = stringResource(titleId), name = name)
}
