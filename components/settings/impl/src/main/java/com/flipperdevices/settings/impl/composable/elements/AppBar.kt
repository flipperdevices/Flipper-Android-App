package com.flipperdevices.settings.impl.composable.elements

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flipperdevices.core.ui.R

@Composable
fun AppBar(@StringRes titleId: Int) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(colorResource(R.color.accent))
    ) {
        Text(
            modifier = Modifier
                .padding(start = 14.dp, end = 14.dp, top = 8.dp, bottom = 11.dp),
            text = stringResource(titleId),
            fontSize = 20.sp,
            fontWeight = FontWeight.W700,
            color = colorResource(R.color.black_100)
        )
    }
}
