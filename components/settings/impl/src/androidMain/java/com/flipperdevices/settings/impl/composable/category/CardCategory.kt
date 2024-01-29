package com.flipperdevices.settings.impl.composable.category

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CardCategory(
    modifier: Modifier = Modifier,
    category: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier.padding(horizontal = 14.dp)
    ) {
        Column {
            category()
        }
    }
}
