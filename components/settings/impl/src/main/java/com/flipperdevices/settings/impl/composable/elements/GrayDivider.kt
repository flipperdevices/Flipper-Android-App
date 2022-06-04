package com.flipperdevices.settings.impl.composable.elements

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.R

@Composable
fun GrayDivider() {
    Divider(
        modifier = Modifier.fillMaxWidth(),
        thickness = 1.dp,
        color = colorResource(R.color.black_12)
    )
}
