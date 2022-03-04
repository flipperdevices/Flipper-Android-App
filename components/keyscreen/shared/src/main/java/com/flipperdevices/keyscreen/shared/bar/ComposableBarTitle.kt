package com.flipperdevices.keyscreen.shared.bar

import androidx.annotation.StringRes
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.flipperdevices.core.ui.R

@Composable
fun ComposableBarTitle(modifier: Modifier, text: String) {
    Text(
        modifier = modifier,
        text = text,
        fontSize = 24.sp,
        fontWeight = FontWeight.W800,
        color = colorResource(R.color.black_88)
    )
}

@Composable
fun ComposableBarTitle(modifier: Modifier, @StringRes textId: Int) {
    ComposableBarTitle(modifier = modifier, text = stringResource(textId))
}
