package com.flipperdevices.keyscreen.shared.content

import com.flipperdevices.core.ui.res.R as DesignSystem
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
private fun ComposableKeyItem(name: String, value: String) {
    Column(
        modifier = Modifier.padding(horizontal = 12.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = name,
            fontWeight = FontWeight.W400,
            fontSize = 16.sp,
            color = colorResource(DesignSystem.color.black_30)
        )
        Text(
            text = value,
            fontWeight = FontWeight.W400,
            fontSize = 16.sp,
            color = colorResource(DesignSystem.color.black_100)
        )
    }
}

@Composable
internal fun ColumnScope.ComposableKeyContent(lines: List<Pair<String, String?>>) {
    lines.filter { it.second != null }.map { it.first to it.second!! }.forEach {
        ComposableKeyItem(it.first, it.second)
    }
}
