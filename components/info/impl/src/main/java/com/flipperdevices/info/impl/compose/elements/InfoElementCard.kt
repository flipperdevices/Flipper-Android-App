package com.flipperdevices.info.impl.compose.elements

import com.flipperdevices.core.ui.res.R as DesignSystem
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun InfoElementCard(
    modifier: Modifier,
    @StringRes titleId: Int? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp),
        shape = RoundedCornerShape(size = 10.dp)
    ) {
        Column(verticalArrangement = Arrangement.SpaceBetween) {
            if (titleId != null) {
                Text(
                    modifier = Modifier.padding(
                        start = 12.dp,
                        end = 12.dp,
                        top = 12.dp,
                        bottom = 6.dp
                    ),
                    text = stringResource(titleId),
                    fontWeight = FontWeight.W700,
                    fontSize = 16.sp,
                    color = colorResource(DesignSystem.color.black_100)
                )
            }
            content()
        }
    }
}
