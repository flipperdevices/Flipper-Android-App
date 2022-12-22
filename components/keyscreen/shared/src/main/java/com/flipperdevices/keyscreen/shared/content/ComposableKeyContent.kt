package com.flipperdevices.keyscreen.shared.content

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.ktx.placeholder
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography

@Composable
private fun ComposableKeyItem(name: String, value: String) {
    Column(
        modifier = Modifier.padding(horizontal = 12.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            modifier = Modifier.placeholder(),
            text = name,
            color = LocalPallet.current.text30,
            style = LocalTypography.current.bodyR16
        )
        Text(
            modifier = Modifier.placeholder(),
            text = value,
            style = LocalTypography.current.bodyR16
        )
    }
}

@Composable
internal fun ComposableKeyContent(lines: List<Pair<String, String?>>) {
    lines.filter { it.second != null }.map { it.first to it.second!! }.forEach {
        ComposableKeyItem(it.first, it.second)
    }
}
