package com.flipperdevices.settings.impl.composable.category

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CardCategory(
    modifier: Modifier = Modifier,
    category: @Composable () -> Unit
) {
    Card(
        modifier = modifier.padding(horizontal = 14.dp),
        content = category
    )
}
