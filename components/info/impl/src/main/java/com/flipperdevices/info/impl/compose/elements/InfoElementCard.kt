package com.flipperdevices.info.impl.compose.elements

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

@Composable
fun InfoElementCard(
    @StringRes titleId: Int,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.padding(horizontal = 14.dp),
        shape = RoundedCornerShape(size = 10.dp)
    ) {
        Column {
            Text(
                modifier = Modifier.padding(
                    start = 12.dp,
                    end = 12.dp,
                    top = 12.dp,
                    bottom = 6.dp
                ),
                text = stringResource(titleId)
            )
            content()
        }
    }
}
