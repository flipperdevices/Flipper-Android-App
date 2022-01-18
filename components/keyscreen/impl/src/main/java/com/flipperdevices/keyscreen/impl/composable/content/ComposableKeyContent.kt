package com.flipperdevices.keyscreen.impl.composable.content

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.flipperdevices.keyscreen.impl.R

@Composable
fun ComposableKeyContent(
    content: @Composable ColumnScope.() -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Divider(
            modifier = Modifier
                .padding(
                    top = 18.dp,
                    bottom = 6.dp // 12 + 6 = 18
                )
                .fillMaxWidth(),
            thickness = 1.dp,
            color = colorResource(R.color.keyscreen_devider)
        )

        content()
    }
}
